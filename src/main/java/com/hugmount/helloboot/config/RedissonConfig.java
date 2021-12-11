package com.hugmount.helloboot.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RedissonConfig {

    @Value("${redis.address}")
    private String address;

    @Value("${redis.password}")
    private String password;


    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.setCodec(new org.redisson.client.codec.StringCodec());
//        config.useSentinelServers() // 哨兵配置
//        .setMasterName("my-sentinel-name") // 设置sentinel.conf配置里的sentinel别名
//         这里设置sentinel节点的服务IP和端口，一般sentinel至少3个节点
//        .addSentinelAddress("redis://127.0.0.1:26379")

        log.info("redisson address: {}", address);
        config.useSingleServer()
                .setPassword(password)
                .setAddress(address);
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
