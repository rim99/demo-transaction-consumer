
package org.example.transaction.consumer;

import io.helidon.common.reactive.Single;
import io.helidon.config.Config;
import io.helidon.dbclient.DbClient;
import io.helidon.health.HealthSupport;
import io.helidon.health.checks.HealthChecks;
import io.helidon.media.jsonp.JsonpSupport;
import io.helidon.metrics.MetricsSupport;
import io.helidon.webserver.Routing;
import io.helidon.webserver.WebServer;
import org.example.transaction.consumer.adapter.*;
import org.example.transaction.consumer.adapter.redis.RedisStorage;
import org.example.transaction.consumer.entity.mapper.AggregationItemListSerializer;
import org.example.transaction.consumer.service.TransactionAggregationHttpServiceImpl;
import org.example.transaction.consumer.service.TransactionAggregationService;
import org.example.transaction.consumer.service.TransactionRecordConsumeService;

import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.concurrent.TimeoutException;
import java.util.logging.LogManager;

public final class Main {

    private Main() {
    }

    public static void main(final String[] args) throws IOException, TimeoutException {
        long start = System.nanoTime();
        Config config = Config.create();
        startMessageReceiver(config);
        startServer(config)
                .thenAccept(ws -> System.out.println(
                        "Application has started in " +
                                Duration.ofNanos(System.nanoTime() - start) +
                                " seconds at PID: " +
                                ManagementFactory.getRuntimeMXBean().getName().split("@")[0] +
                                " on port: " +
                                ws.port())
                )
                .exceptionallyAccept(Throwable::printStackTrace);
    }

    static void startMessageReceiver(Config config) throws IOException, TimeoutException {
        Config rabbitMQConfig = config.get("rabbitmq");
        Config dbConfig = config.get("db");

        DbClient dbClient = DbClient.builder(dbConfig).build();
        RedisStorage redisStorage = RedisStorage.Builder.build();
        RabbitmqMessageReceiver r = RabbitmqMessageReceiver.Builder.build(
                rabbitMQConfig.get("host").asString().get(),
                rabbitMQConfig.get("username").asString().get(),
                rabbitMQConfig.get("password").asString().get(),
                rabbitMQConfig.get("queueName").asString().get()
        );
        r.subscribe(new TransactionRecordConsumeService(
                new TransactionRecordPostgresRepository(dbClient),
                new TransactionAggregationService(new TransactionAggregationRepositoryImpl(redisStorage))
        ));
        r.start();
    }

    /**
     * Start the server.
     * @return the created {@link WebServer} instance
     * @throws IOException if there are problems reading logging properties
     */
    static Single<WebServer> startServer(Config config) throws IOException {
        // load logging configuration
        setupLogging();

        // Build server with JSONP support
        WebServer server = WebServer.builder(createRouting(config))
                .config(config.get("server"))
                .addMediaSupport(JsonpSupport.create())
                .build();
        return server.start();
    }

    /**
     * Creates new {@link Routing}.
     *
     * @return routing configured with JSON support, a health check, and a service
     * @param config configuration of this server
     */
    private static Routing createRouting(Config config) {
        MetricsSupport metrics = MetricsSupport.create();
        HealthSupport health = HealthSupport.builder()
                .addLiveness(HealthChecks.healthChecks())   // Adds a convenient set of checks
                .build();
        TransactionAggregationHttpAPI transactionAggregationHttpAPI =
                new TransactionAggregationHttpAPI(
                        new TransactionAggregationHttpServiceImpl(),
                        new AggregationItemListSerializer()
                );

        return Routing.builder()
                .register(health)                   // Health at "/health"
                .register(metrics)                  // Metrics at "/metrics"
                .register("/transaction-aggregations", transactionAggregationHttpAPI)
                .build();
    }

    /**
     * Configure logging from logging.properties file.
     */
    private static void setupLogging() throws IOException {
        try (InputStream is = Main.class.getResourceAsStream("/logging.properties")) {
            LogManager.getLogManager().readConfiguration(is);
        }
    }
}
