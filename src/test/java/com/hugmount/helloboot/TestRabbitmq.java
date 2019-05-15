package com.hugmount.helloboot;

import com.hugmount.helloboot.util.RabbitmqUtil;

/**
 * @Author: Li Huiming
 * @Date: 2019/5/15
 */
public class TestRabbitmq {
    public static void main(String[] args) {
        RabbitmqUtil rabbitmqUtil = new RabbitmqUtil(
                "192.168.10.180",
                "installment",
                "installment",
                5672,
                "hello",
                "amq.direct");
        rabbitmqUtil.send("hello world");
        rabbitmqUtil.receive();

    }
}
