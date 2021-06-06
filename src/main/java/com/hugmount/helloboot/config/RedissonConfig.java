package com.hugmount.helloboot.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
//        config.useSentinelServers() // 哨兵配置
//        .setMasterName("my-sentinel-name") // 设置sentinel.conf配置里的sentinel别名
//         这里设置sentinel节点的服务IP和端口，一般sentinel至少3个节点
//        .addSentinelAddress("redis://127.0.0.1:26379")
        config.useSingleServer()
                .setPassword("123456")
                .setAddress("redis://115.159.66.110:6379");
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
