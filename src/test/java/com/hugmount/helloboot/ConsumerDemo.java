package com.hugmount.helloboot;

import com.hugmount.helloboot.util.RabbitmqUtil;

/**
 * @Author: Li Huiming
 * @Date: 2019/5/16
 */
public class ConsumerDemo extends RabbitmqUtil.BaseConsumerService {

    @Override
    public void consume(String receiveMsg, String... args) {
        if (receiveMsg.contains("1")) {
//            throw new RuntimeException("报错");
        }
        System.out.println("已经消费信息: " + receiveMsg);
    }
}
