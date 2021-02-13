package com.hugmount.helloboot.annotation;

import java.lang.annotation.*;

/**
 * @Author: Li Huiming
 * @Date: 2019/6/14
 */

@Target(ElementType.TYPE) //可修饰范围
@Retention(RetentionPolicy.RUNTIME) //保留时间: 运行时
@Documented // 标记注解文档
@Inherited //该注解是否可被继承
public @interface LogInfo {

    String path();

    String url() default "";

    String[] params() default {};

    String result() default "success";

}
