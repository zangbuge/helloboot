package com.hugmount.helloboot;

import com.alibaba.fastjson.JSON;
import com.hugmount.helloboot.test.pojo.Test;
import org.apache.commons.lang3.SerializationUtils;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.codec.SerializationCodec;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

public class TestRedisson {
    public static void main(String[] args) {
        Config config = new Config();
        // org.redisson.codec.SerializationCodec JDK序列化编码
        // org.redisson.codec.JsonJacksonCodec   Jackson JSON 编码
        // org.redisson.codec.FstCodec           FST 10倍于JDK序列化性能而且100%兼容的编码   默认编码
        config.setCodec(new SerializationCodec());

        //指定使用单节点部署方式
        config.useSingleServer().setAddress("redis://118.195.172.19:6379");
        config.useSingleServer().setPassword("123456");
        config.useSingleServer().setConnectionPoolSize(500);//设置对于master节点的连接池中连接数最大为500
        config.useSingleServer().setIdleConnectionTimeout(9000);//如果当前连接池里的连接数量超过了最小空闲连接数，而同时有连接空闲时间超过了该数值，那么这些连接将会自动被关闭，并从连接池里去掉。时间单位是毫秒。
        config.useSingleServer().setConnectTimeout(9000);//同任何节点建立连接时的等待超时。时间单位是毫秒。
        config.useSingleServer().setTimeout(3000);//等待节点回复命令的时间。该时间从命令发送成功时开始计时。

        Test test = new Test();
        test.setId(111L);

        RedissonClient redissonClient = Redisson.create(config);
        RBucket<byte[]> bucket = redissonClient.getBucket("lhm");
        bucket.set(SerializationUtils.serialize(test));
        byte[] obj = bucket.get();
        Object deserialize = SerializationUtils.deserialize(obj);
        System.out.println("获取redis值: " + JSON.toJSONString(deserialize));

        RMapCache<String, Object> map = redissonClient.getMapCache("map");
        map.put("lhm", test, 30, TimeUnit.SECONDS);
        Object lhm = map.get("lhm");
        System.out.println(JSON.toJSONString(lhm));
    }

}
