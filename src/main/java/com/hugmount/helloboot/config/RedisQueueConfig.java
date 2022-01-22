package com.hugmount.helloboot.config;

import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * 延时队列持久化在redis中，所以机器宕机数据不会异常丢失，机器重启后，会正常消费队列中积累的任务
 */
@Profile("prod")
@Component
public class RedisQueueConfig {

    private final String queueName = "queue";

    @Bean
    public RBlockingQueue<String> rBlockingQueue(@Qualifier("redissonClient") RedissonClient redissonClient) {
        return redissonClient.getBlockingQueue(queueName);
    }

    @Bean
    public RDelayedQueue<String> rDelayedQueue(@Qualifier("redissonClient") RedissonClient redissonClient,
                                               @Qualifier("rBlockingQueue") RBlockingQueue<String> blockQueue) {
        return redissonClient.getDelayedQueue(blockQueue);
    }

}
