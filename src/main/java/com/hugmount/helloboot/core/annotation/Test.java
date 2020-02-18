package com.hugmount.helloboot.core.annotation;


import com.ajiao.annotation.test.Hello;

/** 测试自定义注解, jitpack作为仓库.
 * https://jitpack.io/#zangbuge/ajiao/0.0.2-SNAPSHOT
 * @Author Li Huiming
 * @Date 2020/2/18
 */

@Hello
public class Test {
    public static void main(String[] args) {
        TestHello.sayHello();
    }
}
