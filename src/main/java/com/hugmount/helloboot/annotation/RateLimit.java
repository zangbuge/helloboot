package com.hugmount.helloboot.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解限流
 *
 * @author: lhm
 * @date: 2023/8/8
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    /**
     * key
     *
     * @return
     */
    String limitKey();

    /**
     * tps
     *
     * @return
     */
    int limitCount() default 1000;
}
