package com.hugmount.helloboot;

import com.hugmount.helloboot.util.RabbitmqUtil;
import com.rabbitmq.client.BuiltinExchangeType;

/**
 * @Author: Li Huiming
 * @Date: 2019/5/15
 */
public class TestRabbitmqDLX {
    public static void main(String[] args) {
        String ip = "127.0.0.1";
        int port = 5672;
        String username = "lhm";
        String password = "123456";
        String vhost = "test";

        String exchangeName = "test_dlx_exchange_1";
        String queueName = "test_dlx_queue_1";
        String router = "test_dlx_router_1";

        // 生产者发布消息的时候,可以自动创建队列, 订阅者在启动的时候绑定的队列必须是已存在(或先启动生产者程序)
        RabbitmqUtil.createConnection(ip, port, username, password, vhost);
        RabbitmqUtil.sendMsg(exchangeName, BuiltinExchangeType.DIRECT.getType(), queueName ,router ,"我就试试");
        ConsumerDemo consumerDemo = new ConsumerDemo();
        RabbitmqUtil.receive(exchangeName, queueName, router,consumerDemo);
        System.out.println("死信队列");
//        RabbitmqUtil.sendMsg(exchangeName, queueName ,router ,"我就试试222");

    }
}
