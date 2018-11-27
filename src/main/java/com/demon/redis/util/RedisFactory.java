package com.demon.redis.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisFactory {

    public static final RedisFactory instance = new RedisFactory();
    private JedisPool pool;

    {
        pool = new JedisPool("127.0.0.1");
    }

    public Jedis getJedisInstance() {
        return pool.getResource();
    }

    public void returnResource(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
            jedis = null;
        }
    }
}
