package org.example.transaction.consumer.adapter.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

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

    public static class Builder {
        public static RedisStorage build() {
            JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
            Runtime.getRuntime().addShutdownHook(new Thread(pool::close));
            return new RedisStorage(pool);
        }
    }
}
