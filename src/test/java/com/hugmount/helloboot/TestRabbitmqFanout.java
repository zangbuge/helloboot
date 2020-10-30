package com.hugmount.helloboot;

import com.hugmount.helloboot.util.RabbitmqUtil;

/** 广播模式
 * @Author: Li Huiming
 * @Date: 2019/5/15
 */
public class TestRabbitmqFanout {
    public static void main(String[] args) {
        String ip = "127.0.0.1";
        int port = 5672;
        String username = "lhm";
        String password = "123456";
        String vhost = "test";

        String exchangeName = "testFanout";
        String exchangeType = "fanout";
        // testFanout1 testFanout2
        String queueName = "testFanout2";
        String router = "fanout";

        RabbitmqUtil.createConnection(ip, port, username, password, vhost);
        System.out.println("队列: " + queueName);
        ConsumerDemo consumerDemo = new ConsumerDemo();
//        RabbitmqUtil.receive(exchangeName, queueName, router, consumerDemo);
        // queueName, router 不会匹配, 但不可传null, 可以传空串 ""
        RabbitmqUtil.sendMsg(exchangeName, exchangeType, "" ,"" ,"我试试广播");


    }
}
