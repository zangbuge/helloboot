package com.hugmount.helloboot;

import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class TestRedisson {
    public static void main(String[] args) {
        Config config = new Config();
        config.setCodec(new org.redisson.client.codec.StringCodec());

        //指定使用单节点部署方式
        config.useSingleServer().setAddress("redis://115.159.66.110:6379");
        config.useSingleServer().setPassword("123456");
        config.useSingleServer().setConnectionPoolSize(500);//设置对于master节点的连接池中连接数最大为500
        config.useSingleServer().setIdleConnectionTimeout(9000);//如果当前连接池里的连接数量超过了最小空闲连接数，而同时有连接空闲时间超过了该数值，那么这些连接将会自动被关闭，并从连接池里去掉。时间单位是毫秒。
        config.useSingleServer().setConnectTimeout(9000);//同任何节点建立连接时的等待超时。时间单位是毫秒。
        config.useSingleServer().setTimeout(3000);//等待节点回复命令的时间。该时间从命令发送成功时开始计时。

        RedissonClient redissonClient = Redisson.create(config);
        RBucket<Object> bucket = redissonClient.getBucket("lhm");
        bucket.set("lihuiming");
        Object val = bucket.get();
        System.out.println("获取redis值: " + val);
    }

}
