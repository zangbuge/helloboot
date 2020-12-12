package com.hugmount.helloboot;

import com.hugmount.helloboot.util.RabbitmqUtil;

/**
 * @Author: Li Huiming
 * @Date: 2019/5/15
 */
public class TestRabbitmq {
    public static void main(String[] args) {
        String ip = "127.0.0.1";
        int port = 5672;
        String username = "lhm";
        String password = "123456";
        String vhost = "test";

        String exchangeName = "hello2";
        String queueName = "hello2";
        String router = "hello2";

        // 生产者发布消息的时候,可以自动创建队列, 订阅者在启动的时候绑定的队列必须是已存在(或先启动生产者程序)
        RabbitmqUtil.createConnection(ip, port, username, password, vhost);
        RabbitmqUtil.sendMsg(exchangeName, null, queueName ,router ,"我就试试死信", "9000", "test_dlx_exchange_1", "test_dlx_router_1");
//        ConsumerDemo consumerDemo = new ConsumerDemo();
//        RabbitmqUtil.receive(exchangeName, queueName, router,consumerDemo);

//        RabbitmqUtil.sendMsg(exchangeName, queueName ,router ,"我就试试222");

//        RabbitmqUtil.sendMsg(exchangeName, queueName ,"good" ,"换个路由, 我就试试33");

        // 本地测试 不关闭channel 达到2044个管道时资源耗尽异常
       /* long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            RabbitmqUtil.sendMsg(exchangeName, queueName ,router ,"我就试试RabbitmqUtil " + i);
        }
        long end = System.currentTimeMillis();
        System.out.println("urtil用时: " + (end - start));*/
    }
}
