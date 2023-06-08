package com.hugmount.helloboot.test.controller;

import com.hugmount.helloboot.core.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;

/**
 * @author: lhm
 * @date: 2023/6/8
 */
@Slf4j
@RestController
public class RedismqController {

    String key = "mq";

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/push")
    public Result<String> push(String msg) {
        redisTemplate.opsForList().leftPush(key, msg);
        return Result.createBySuccess("成功");
    }

    @PostConstruct
    public void initMq() {
        log.info("mq消费已启动");
        CompletableFuture.runAsync(() -> {
            for (; ; ) {
                Object obj = redisTemplate.opsForList().rightPop(key);
                consumer(obj);
            }
        });
    }

    public void consumer(Object obj) {
        try {
            log.info("消费信息: {}", obj);
            if (ObjectUtils.isEmpty(obj)) {
                Thread.sleep(1000 * 5);
            }
        } catch (Exception e) {
            log.error("mq消费异常", e);
        }
    }
}
