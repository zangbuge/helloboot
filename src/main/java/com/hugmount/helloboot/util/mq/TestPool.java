package com.hugmount.helloboot.util.mq;

/**
 * @Author: Li Huiming
 * @Date: 2020/8/13
 */
public class TestPool {
    public static void main(String[] args) {
        String ip = "127.0.0.1";
        int port = 5672;
        String username = "lhm";
        String password = "123456";
        String vhost = "test";

        PoolProperties poolProperties = new PoolProperties();
        poolProperties.setUrl(ip);
        poolProperties.setPort(port);
        poolProperties.setUser(username);
        poolProperties.setPassword(password);
        poolProperties.setVHost(vhost);
        RabbitmqHeadler.init(poolProperties);

        String exchangeName = "hello";
        String queueName = "abdc";
        String router = "hello";
        // 消息轮询分配给消费者消费
        RabbitmqHeadler.sendMsg(exchangeName, "direct", queueName, router, "测试用例");

        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            RabbitmqHeadler.sendMsg(exchangeName, "direct", queueName, router, "测试用例RabbitmqHeadler " + i);
        }
        long end = System.currentTimeMillis();
        System.out.println("RabbitmqHeadler用时: " + (end - start));
    }
}
