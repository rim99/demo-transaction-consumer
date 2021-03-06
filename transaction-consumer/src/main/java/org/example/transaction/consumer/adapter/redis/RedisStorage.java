package org.example.transaction.consumer.adapter.redis;

import io.lettuce.core.Range;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Histogram;
import org.example.transaction.consumer.port.AggregationItem;

import javax.inject.Singleton;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Singleton
public class RedisStorage {

    private StatefulRedisConnection<String, String> connection;

    private RedisStorage(StatefulRedisConnection<String, String> connection) {
        this.connection = connection;
    }

    public CompletableFuture<Double> add(RedisZsetIndex index, double amount) {
        CompletableFuture<Double> result = new CompletableFuture<>();
        Histogram.Timer t = persistTimer.startTimer();
        try {
            RedisAsyncCommands<String, String> asyncCommands = connection.async();
            String key = index.getZsetName() + index.getMemberName();
            asyncCommands.zadd(index.getZsetName(), index.getScore(), index.getMemberName())
                    .thenAcceptBoth(
                            asyncCommands.incrbyfloat(key, amount),
                            (addedCount, incredVal) -> {
                                t.observeDuration();
                                result.complete(incredVal);
                            }
            );
        } catch (Exception e) {
            System.out.println("Error when add amount [" + amount + "] for " + index + ", detail: " + e);
        }
        return result;
    }

    public CompletableFuture<Long> addOne(RedisZsetIndex index) {
        CompletableFuture<Long> result = new CompletableFuture<>();
        Histogram.Timer t = persistTimer.startTimer();
        try {
            RedisAsyncCommands<String, String> asyncCommands = connection.async();
            String key = index.getZsetName() + index.getMemberName();
            asyncCommands.zadd(index.getZsetName(), index.getScore(), index.getMemberName())
                    .thenAcceptBoth(
                            asyncCommands.incr(key),
                            (addedCount, incredVal) -> {
                                t.observeDuration();
                                result.complete(incredVal);
                            });
        } catch (Exception e) {
            System.out.println("Error when add one for " + index + ", detail: " + e);
        }
        return result;
    }

    public Set<AggregationItem> getAll(RedisZsetIndex start, RedisZsetIndex stop) {
        Histogram.Timer t = persistTimer.startTimer();
        Set<AggregationItem> result = null;
        try {
            RedisCommands<String, String> syncCommands = connection.sync();
            Range<Double> range = Range.create(start.getScore(), stop.getScore());
            List<String> keys = syncCommands.zrangebyscore(start.getZsetName(), range);
            result = keys.stream().map(k -> {
                String stamp = k.contains("_") ? k.split("_")[1] : k;
                Double value = Double.valueOf(syncCommands.get(start.getZsetName()+k));
                return new AggregationItem(stamp, value);
            }).collect(Collectors.toSet());
            t.observeDuration();
        } catch (Exception e) {
            System.out.println("Error when get aggregation summaries from " + start + " to " + stop + ", detail: " + e);
        }
        return result;
    }

    public static class Builder {
        public static RedisStorage build(String host, Integer port) {
            RedisClient client = RedisClient.create(RedisURI.create(host, port));
            StatefulRedisConnection<String, String> connection = client.connect();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                connection.close();
                client.shutdown();
            }));
            return new RedisStorage(connection);
        }
    }


    private static final Histogram retrieveTimer;
    private static final Histogram persistTimer;
    static {
        retrieveTimer = Histogram
                .build("redis_retrieve_response_time", "response time of redis when retrieving")
                .create();
        persistTimer = Histogram
                .build("redis_persist_response_time", "response time of redis when persisting")
                .create();
        CollectorRegistry.defaultRegistry.register(persistTimer);
        CollectorRegistry.defaultRegistry.register(retrieveTimer);
    }
}
