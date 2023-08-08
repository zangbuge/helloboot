package com.hugmount.helloboot.annotation;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 基于guava实现限流
 *
 * @author: lhm
 * @date: 2023/8/8
 */

@Slf4j
@Aspect
@Component
public class RateLimitAop {

    @Before(value = "@annotation(com.hugmount.helloboot.annotation.RateLimit)")
    public void limit(JoinPoint joinPoint) {
        Method currentMethod = getCurrentMethod(joinPoint);
        if (Objects.isNull(currentMethod)) {
            return;
        }
        String limitKey = currentMethod.getAnnotation(RateLimit.class).limitKey();
        double limitCount = currentMethod.getAnnotation(RateLimit.class).limitCount();
        //使用guava的令牌桶算法获取一个令牌，获取不到先等待
        RateLimiter rateLimiter = RateLimitHelper.getRateLimiter(limitKey, limitCount);
        Assert.isTrue(rateLimiter.tryAcquire(), "限流中");
    }

    private Method getCurrentMethod(JoinPoint joinPoint) {
        Method[] methods = joinPoint.getTarget().getClass().getMethods();
        String curMethodName = joinPoint.getSignature().getName();
        for (Method method : methods) {
            if (method.getName().equals(curMethodName)) {
                return method;
            }
        }
        return null;
    }
}
