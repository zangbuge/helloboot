package com.hugmount.helloboot;

import com.hugmount.helloboot.util.RabbitmqUtil;
import com.rabbitmq.client.Connection;

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
        String queueName = "abdc";
        Connection connection = RabbitmqUtil.createConnection(ip, port, username, password, vhost);
        RabbitmqUtil.sendMsg(connection, "" ,queueName ,"我就试试");
        ConsumerDemo consumerDemo = new ConsumerDemo();
        RabbitmqUtil.receive(queueName ,consumerDemo);

        RabbitmqUtil.sendMsg("" ,queueName ,"我就试试222");

    }
}
