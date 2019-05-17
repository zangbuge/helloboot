package com.hugmount.helloboot;

import com.hugmount.helloboot.util.RabbitmqUtil;

/**
 * @Author: Li Huiming
 * @Date: 2019/5/16
 */
public class ConsumerDemo extends RabbitmqUtil.ConsumerService {
    public void consume(String receiveMsg, String... args) {
        System.out.println("我已经消费了哦, 信息: " + receiveMsg);
    }
}
