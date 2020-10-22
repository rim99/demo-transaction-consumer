package org.example.transaction.consumer.adapter.redis;

import org.example.transaction.consumer.port.AggregationItem;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.inject.Singleton;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class RedisStorage {

    private JedisPool pool;

    private RedisStorage(JedisPool pool) {
        this.pool = pool;
    }

    public double add(RedisZsetIndex index, double amount) {
        double added = 0D;
        try (Jedis jedis = pool.getResource()) {
            jedis.zadd(index.getZsetName(), index.getScore(), index.getMemberName());
            String key = index.getZsetName() + index.getMemberName();
            added = jedis.incrByFloat(key, amount);
        } catch (Exception e) {
            System.out.println("Error when add amount [" + amount + "] for " + index + ", detail: " + e);
        }
        return added;
    }

    public double addOne(RedisZsetIndex index) {
        long i = 0L;
        try (Jedis jedis = pool.getResource()) {
            jedis.zadd(index.getZsetName(), index.getScore(), index.getMemberName());
            String key = index.getZsetName() + index.getMemberName();
            i = jedis.incr(key);
        } catch (Exception e) {
            System.out.println("Error when add one for " + index + ", detail: " + e);
        }
        return i;
    }

    public Set<AggregationItem> getAll(RedisZsetIndex start, RedisZsetIndex stop) {
        Set<AggregationItem> result = null;
        try (Jedis jedis = pool.getResource()) {
            Set<String> keys = jedis.zrangeByScore(start.getZsetName(), start.getScore(), stop.getScore());
            result = keys.stream().map(k -> {
                String stamp = k.contains("_") ? k.split("_")[1] : k;
                Double value = Double.valueOf(jedis.get(start.getZsetName()+k));
                return new AggregationItem(stamp, value);
            }).collect(Collectors.toSet());
        } catch (Exception e) {
            System.out.println("Error when get aggregation summaries from " + start + " to " + stop + ", detail: " + e);
        }
        return result;
    }

    public static class Builder {
        public static RedisStorage build() {
            JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
            Runtime.getRuntime().addShutdownHook(new Thread(pool::close));
            return new RedisStorage(pool);
        }
    }
}
