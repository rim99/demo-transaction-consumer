package org.example.transaction.consumer.adapter.redis;

import io.lettuce.core.Range;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.example.transaction.consumer.port.AggregationItem;

import javax.inject.Singleton;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class RedisStorage {

    private StatefulRedisConnection<String, String> connection;

    private RedisStorage(StatefulRedisConnection<String, String> connection) {
        this.connection = connection;
    }

    public double add(RedisZsetIndex index, double amount) {
        double added = 0D;
        try {
            RedisCommands<String, String> syncCommands = connection.sync();
            syncCommands.zadd(index.getZsetName(), index.getScore(), index.getMemberName());
            String key = index.getZsetName() + index.getMemberName();
            added = syncCommands.incrbyfloat(key, amount);
        } catch (Exception e) {
            System.out.println("Error when add amount [" + amount + "] for " + index + ", detail: " + e);
        }
        return added;
    }

    public double addOne(RedisZsetIndex index) {
        long i = 0L;
        try {
            RedisCommands<String, String> syncCommands = connection.sync();
            syncCommands.zadd(index.getZsetName(), index.getScore(), index.getMemberName());
            String key = index.getZsetName() + index.getMemberName();
            i = syncCommands.incr(key);
        } catch (Exception e) {
            System.out.println("Error when add one for " + index + ", detail: " + e);
        }
        return i;
    }

    public Set<AggregationItem> getAll(RedisZsetIndex start, RedisZsetIndex stop) {
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
}
