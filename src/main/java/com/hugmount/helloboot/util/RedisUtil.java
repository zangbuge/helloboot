package com.hugmount.helloboot.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.params.SetParams;

import java.util.concurrent.TimeUnit;

/**
 * @author lhm
 * @date 2025/2/23
 */
public class RedisUtil {
    private static final String REDIS_HOST = "127.0.0.1";
    private static final int REDIS_PORT = 6379;
    private static final String REDIS_PASSWORD = null;

    private static JedisPool jedisPool;

    static {
        /**
         * 集群模式 Set<HostAndPort> nodes = new HashSet<>();
         *         nodes.add(new HostAndPort("host", 6379));
         *         // 重载的方法可设置密码
         *         JedisCluster jedisCluster = new JedisCluster(nodes);
         *         jedisCluster.set("key", "val");
         */
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128); // 设置连接池最大连接数
        poolConfig.setMaxIdle(16);   // 设置连接池中的最大空闲连接
        poolConfig.setMinIdle(4);    // 设置连接池中的最小空闲连接
        poolConfig.setTestOnBorrow(true); // 从池中取出连接时，是否进行有效性检查
        poolConfig.setTestOnReturn(true); // 在归还连接时检查有效性
        jedisPool = new JedisPool(poolConfig, REDIS_HOST, REDIS_PORT, 30000, REDIS_PASSWORD);
    }

    public Jedis getJedis() {
        return jedisPool.getResource();
    }

    public static void main(String[] args) {
        set("lhm1", "test1", 3, TimeUnit.SECONDS);
        String lhm1 = get("lhm1");
        System.out.println(lhm1);
        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String lhme = get("lhm1");
        System.out.println(lhme);
    }

    public static void set(String k, String v) {
        Jedis jedis = jedisPool.getResource();
        jedis.set(k, v);
    }

    /**
     * 同时设置过期时间
     *
     * @param k
     * @param v
     * @param ex 单位秒
     */
    private static void set(String k, String v, long ex) {
        SetParams setParams = new SetParams();
        setParams.ex(ex);
        Jedis jedis = jedisPool.getResource();
        jedis.set(k, v, setParams);
    }

    public static void set(String k, String v, long expire, TimeUnit timeUnit) {
        long ex = timeUnit.toSeconds(expire);
        set(k, v, ex);
    }

    public static String get(String k) {
        Jedis jedis = jedisPool.getResource();
        return jedis.get(k);
    }

    public static void del(String k) {
        Jedis jedis = jedisPool.getResource();
        jedis.del(k);
    }

}
