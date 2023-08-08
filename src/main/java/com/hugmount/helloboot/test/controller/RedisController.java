package com.hugmount.helloboot.test.controller;

import com.hugmount.helloboot.core.Result;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.redisson.client.codec.IntegerCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Profile("prod")
@Slf4j
@RestController
public class RedisController {

    @Autowired
    RedissonClient redissonClient;

    @Resource(name = "rDelayedQueue")
    private RDelayedQueue<String> rDelayedQueue;

    @ResponseBody
    @RequestMapping("/testRedis")
    public String testRedis() {
        // 默认非公平锁
        // 非公平锁：多个线程去获取锁的时候，会直接去尝试获取，获取不到，再去进入等待队列，如果能获取到，就直接获取到锁
        // 优点：可以减少CPU唤醒线程的开销，整体的吞吐效率会高点
        RLock rLock = redissonClient.getLock("lock_key_" + "productId");
        try {
            rLock.lock(10, TimeUnit.SECONDS);
            log.info("执行任务中");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }

        // 公平锁
        // 多个线程按照申请锁的顺序去获得锁，线程会直接进入队列去排队，永远都是队列的第一位才能得到锁
        // 优点 所有的线程都能得到资源，不会饿死在队列中
        // 缺点 吞吐量会下降很多，队列里面除了第一个线程，其他的线程都会阻塞，cpu唤醒阻塞线程的开销会很大
        RLock key = redissonClient.getFairLock("key");
        return "success";
    }


    /**
     * 读写锁
     * 一次只有一个线程可以占有写模式 的读写锁, 但是可以有多个线程同时占有读模式 的读写锁
     * 读写锁适合对数据结构的读大于写很多的情况. 因为, 读模式锁定时可以共享, 以写模式锁住时意味着独占, 所以读写锁又叫共享-独占锁.
     * 无论是读请求先执行还是写请求先执行，只要涉及到写锁，则都会阻塞，如果是先写再读，则读锁等待，如果是先读再写，则写锁等待
     *
     * @return
     */
    @GetMapping("/read")
    @ResponseBody
    public String read() {
        RReadWriteLock orderId = redissonClient.getReadWriteLock("orderId");
        RLock rLock = orderId.readLock();
        try {
            rLock.lock();
            RBucket<Object> bucket = redissonClient.getBucket("lhm");
            Object obj = bucket.get();
            // 模拟耗时
//            Thread.sleep(9000);
            return obj.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }
        return "error";
    }


    @GetMapping("/write/{name}")
    @ResponseBody
    public String write(@PathVariable("name") String name) {
        RReadWriteLock orderId = redissonClient.getReadWriteLock("orderId");
        RLock rLock = orderId.writeLock();
        try {
            int incr = getIncr();
            log.info("生成分布式ID为: {}", incr);

            rLock.lock();
            RBucket<Object> bucket = redissonClient.getBucket("lhm");
            bucket.set(name);
            // 模拟耗时
            Thread.sleep(9000);
            return name;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }
        return "error";
    }

    public Integer getIncr() {
        RMap<Object, Integer> nameSpace = redissonClient.getMap("name_space", IntegerCodec.INSTANCE);
        // 业务号段的key
        String seqKey = "order_seq";
        nameSpace.putIfAbsent(seqKey, 0);
        // 加1并获取计算后的值
        Integer id = nameSpace.addAndGet(seqKey, 1);
        return id;
    }

    @ResponseBody
    @GetMapping("/offerAsync")
    public Result<String> offerAsync() {
        log.info("redis延迟队列");
        rDelayedQueue.offerAsync("hello redis delayed queue", 5, TimeUnit.SECONDS);
        return Result.createBySuccess("success");
    }

    @ResponseBody
    @GetMapping("/rateLimiter")
    public Result<String> rateLimiter() {
        log.info("redisson限流");
        RRateLimiter rateLimiter = redissonClient.getRateLimiter("myRateLimiter");
        // 最大流速 = 每1秒钟产生3个令牌, 对同一个redis服务端，只需要设置一次。如果redis重启需要重新设置
        rateLimiter.trySetRate(RateType.OVERALL, 3, 1, RateIntervalUnit.SECONDS);
        boolean b = rateLimiter.tryAcquire();
        Assert.isTrue(b, "redis限流");
        return Result.createBySuccess("success");
    }


}
