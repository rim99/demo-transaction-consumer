package org.example.transaction.consumer.adapter;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisStorage {

    private JedisPool pool;

    private RedisStorage(JedisPool pool) {
        this.pool = pool;
    }

    public double add(String key, double amount) {
        double added = 0D;
        try (Jedis jedis = pool.getResource()) {
            added = jedis.incrByFloat(key, amount);
        } catch (Exception e) {
            System.out.println("Error when add amount [" + amount + "] for key [" + key + "], detail: " + e);
        }
        return added;
    }

    public long addOne(String key) {
        long i = 0L;
        try (Jedis jedis = pool.getResource()) {
            i = jedis.incr(key);
        } catch (Exception e) {
            System.out.println("Error when add one for key [" + key + "], detail: " + e);
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
