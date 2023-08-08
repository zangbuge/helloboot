package com.hugmount.helloboot.annotation;


import com.google.common.util.concurrent.RateLimiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: lhm
 * @date: 2023/8/8
 */
public class RateLimitHelper {

    private RateLimitHelper() {
    }

    private static Map<String, RateLimiter> rateMap = new ConcurrentHashMap<>();

    public static RateLimiter getRateLimiter(String limitKey, double limitCount) {
        RateLimiter rateLimiter = rateMap.get(limitKey);
        if (rateLimiter == null) {
            rateLimiter = RateLimiter.create(limitCount);
            rateMap.put(limitKey, rateLimiter);
        }
        return rateLimiter;
    }
}
