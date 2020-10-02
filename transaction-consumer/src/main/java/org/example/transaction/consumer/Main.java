
package org.example.transaction.consumer;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeoutException;
import java.util.logging.LogManager;

import org.example.transaction.consumer.adapter.RabbitmqMessageReceiver;
import org.example.transaction.consumer.adapter.RedisStorage;
import org.example.transaction.consumer.adapter.TransactionAggregationRepositoryImpl;
import org.example.transaction.consumer.adapter.TransactionRecordPostgresRepository;
import org.example.transaction.consumer.service.TransactionAggregationService;
import org.example.transaction.consumer.service.TransactionRecordConsumeService;

import io.helidon.config.Config;
import io.helidon.dbclient.DbClient;
import io.helidon.health.HealthSupport;
import io.helidon.health.checks.HealthChecks;
import io.helidon.media.jsonp.JsonpSupport;
import io.helidon.metrics.MetricsSupport;
import io.helidon.webserver.Routing;
import io.helidon.webserver.WebServer;
import org.slf4j.LoggerFactory;

/**
 * The application main class.
 */
public final class Main {

    /**
     * Cannot be instantiated.
     */
    private Main() {
    }

    /**
     * Application main entry point.
     * 
     * @param args command line arguments.
     * @throws IOException      if there are problems reading logging properties
     * @throws TimeoutException
     */
    public static void main(final String[] args) throws IOException, TimeoutException {
        // By default this will pick up application.yaml from the classpath
        Config config = Config.create();
        startMessageReceiver(config);
        //startServer(config);
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
        LoggerFactory.getLogger("Main").info("started");
        r.start();
    }

    /**
     * Start the server.
     * @return the created {@link WebServer} instance
     * @throws IOException if there are problems reading logging properties
     */
    static WebServer startServer(Config config) throws IOException {
        // load logging configuration
        setupLogging();

        // Build server with JSONP support
        WebServer server = WebServer.builder(createRouting(config))
                .config(config.get("server"))
                .addMediaSupport(JsonpSupport.create())
                .build();

        // Try to start the server. If successful, print some info and arrange to
        // print a message at shutdown. If unsuccessful, print the exception.
        server.start()
                .thenAccept(ws -> {
                    System.out.println(
                            "WEB server is up! http://localhost:" + ws.port() + "/greet");
                    ws.whenShutdown().thenRun(()
                            -> System.out.println("WEB server is DOWN. Good bye!"));
                })
                .exceptionally(t -> {
                    System.err.println("Startup failed: " + t.getMessage());
                    t.printStackTrace(System.err);
                    return null;
                });

        // Server threads are not daemon. No need to block. Just react.

        return server;
    }

    /**
     * Creates new {@link Routing}.
     *
     * @return routing configured with JSON support, a health check, and a service
     * @param config configuration of this server
     */
    private static Routing createRouting(Config config) {
        MetricsSupport metrics = MetricsSupport.create();
        GreetService greetService = new GreetService(config);
        HealthSupport health = HealthSupport.builder()
                .addLiveness(HealthChecks.healthChecks())   // Adds a convenient set of checks
                .build();

        return Routing.builder()
                .register(health)                   // Health at "/health"
                .register(metrics)                  // Metrics at "/metrics"
                .register("/greet", greetService)
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
