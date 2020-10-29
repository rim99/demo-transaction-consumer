package org.example.transaction.consumer;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.helidon.common.reactive.Single;
import io.helidon.config.Config;
import io.helidon.health.HealthSupport;
import io.helidon.health.checks.HealthChecks;
import io.helidon.media.jsonp.JsonpSupport;
import io.helidon.metrics.MetricsSupport;
import io.helidon.metrics.prometheus.PrometheusSupport;
import io.helidon.webserver.Routing;
import io.helidon.webserver.WebServer;
import org.example.transaction.consumer.adapter.RabbitmqMessageReceiver;
import org.example.transaction.consumer.adapter.TransactionAggregationHttpAPI;
import org.example.transaction.consumer.config.AppModule;
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
        Injector injector = Guice.createInjector(new AppModule());
        startMessageReceiver(config, injector);
        startServer(config, injector)
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

    static void startMessageReceiver(Config config, Injector injector) throws IOException, TimeoutException {
        Config rabbitMQConfig = config.get("rabbitmq");
        RabbitmqMessageReceiver r = RabbitmqMessageReceiver.Builder.build(
                rabbitMQConfig.get("host").asString().get(),
                rabbitMQConfig.get("username").asString().get(),
                rabbitMQConfig.get("password").asString().get(),
                rabbitMQConfig.get("queueName").asString().get()
        );
        r.subscribe(injector.getInstance(TransactionRecordConsumeService.class));
        r.start();
    }

    static Single<WebServer> startServer(Config config, Injector injector) throws IOException {
        setupLogging();

        WebServer server = WebServer.builder(createRouting(injector))
                .config(config.get("server"))
                .addMediaSupport(JsonpSupport.create())
                .build();
        return server.start();
    }

    private static Routing createRouting(Injector injector) {
        HealthSupport health = HealthSupport.builder()
                .addLiveness(HealthChecks.healthChecks())   // Adds a convenient set of checks
                .build();
        return Routing.builder()
                .register(health)                      // Health at "/health"
                .register(PrometheusSupport.create())  // Metrics at "/metrics"
                .register(MetricsSupport.builder().webContext("/jvm-metrics").build())
                .register("/transaction-aggregations",
                        injector.getInstance(TransactionAggregationHttpAPI.class))
                .build();
    }

    private static void setupLogging() throws IOException {
        try (InputStream is = Main.class.getResourceAsStream("/logging.properties")) {
            LogManager.getLogManager().readConfiguration(is);
        }
    }
}
