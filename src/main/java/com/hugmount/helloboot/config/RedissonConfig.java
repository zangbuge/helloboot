package com.hugmount.helloboot.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${spring.redis.host}")
    private String idAddr;

    @Value("${spring.redis.port}")
    private String port;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.timeout}")
    private String timeout;


    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.setCodec(new org.redisson.client.codec.StringCodec());
//        config.useSentinelServers() // 哨兵配置
//        .setMasterName("my-sentinel-name") // 设置sentinel.conf配置里的sentinel别名
//         这里设置sentinel节点的服务IP和端口，一般sentinel至少3个节点
//        .addSentinelAddress("redis://127.0.0.1:26379")

        String addr = "redis://" + idAddr + ":" + port;
        config.useSingleServer()
                .setTimeout(Integer.parseInt(timeout))
                .setPassword(password)
                .setAddress(addr);
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
