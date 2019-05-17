package com.hugmount.helloboot;

import com.hugmount.helloboot.util.RabbitmqUtil;

/**
 * @Author: Li Huiming
 * @Date: 2019/5/15
 */
public class TestRabbitmq {
    public static void main(String[] args) {
        String ip = "192.168.10.180";
        int port = 5672;
        String username = "installment";
        String password = "installment";
        String vhost = "/installment";

        String exchangeName = "hello";
        String queueName = "abdc";
        String router = "lhm";

        RabbitmqUtil.createConnection(ip, port, username, password, vhost);
        RabbitmqUtil.sendMsg(exchangeName, queueName ,router ,"我就试试");
        ConsumerDemo consumerDemo = new ConsumerDemo();
        RabbitmqUtil.receive(exchangeName, queueName, router,consumerDemo);

        RabbitmqUtil.sendMsg(exchangeName, queueName ,router ,"我就试试222");

        RabbitmqUtil.sendMsg(exchangeName, queueName ,"good" ,"换个路由, 我就试试33");
    }
}
