package com.hugmount.helloboot.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

/**
 * @author: lhm
 * @date: 2022/6/29
 */
public class LocalCacheUtil {

    private static Cache<String, Object> cache;

    static {
        /**
         * 设置缓存时间
         */
        cache = CacheBuilder.newBuilder().expireAfterAccess(7, TimeUnit.DAYS).maximumSize(Long.MAX_VALUE / 3).build();
    }

    public static void put(String key, Object val) {
        cache.put(key, val);
    }

    public static Object get(String key) {
        return cache.asMap().get(key);
    }

    public static void delete(String key) {
        cache.invalidate(key);
    }

}
