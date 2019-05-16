package com.hugmount.helloboot.util;

import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Author: Li Huiming
 * @Date: 2019/5/15
 */

@Slf4j
public class RabbitmqUtil {

    private static ConnectionFactory connectionFactory = null;

    private static Connection connection = null;

    public static Connection createConnection (String ip, int port, String username, String password, String vHost) {
        if (null != connection) {
            log.info("rabbitmq已经初始化了");
            return connection;
        }

        connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(ip);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(vHost);

        try {
            connection = connectionFactory.newConnection();
            log.info("创建 rabbitmq connection success !");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return connection;
    }


    public static void sendMsg (String exchangeName, String queueName ,String msg) {
        sendMsg(connection, exchangeName, queueName, msg);
    }

    public static void sendMsg (Connection connection, String exchangeName, String queueName ,String msg) {
        try {
            //创建一个通道
            Channel channel = connection.createChannel();
            //声明一个队列, 如果不存在则创建, true表示持久化
            channel.queueDeclare(queueName,true, false, false, null);
            //推送消息到队列中
            channel.basicPublish(exchangeName ,queueName ,null, msg.getBytes("UTF-8"));
            log.info("消息推送到 rabbitmq success !");

            //关闭通道和连接
//            channel.close();
//            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void receive (String queueName, final ConsumerService consumerService) {
        receive(connection, queueName, consumerService);
    }

    /**
     * 该方法只用初始化一次即可
     * @param connection
     * @param queueName
     * @param consumerService
     */
    public static void receive (Connection connection, String queueName, final ConsumerService consumerService) {
        log.info("rabbitmq初始化消费...");
        try {
            //创建一个通道
            Channel channel = connection.createChannel();
            //声明要关注的队列
            channel.queueDeclare(queueName,true, false, false, null);
            //定义一个消费者DefaultConsumer, 如果通道中有消息,就会执行回调函数handleDelivery
            Consumer consumer = new DefaultConsumer(channel) {
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body)
                        throws IOException {

                    log.info("rabbitmq开始消费...");
                    String msg = new String(body, "UTF-8");
                    consumerService.consume(queueName ,msg);
                    log.info("rabbitmq消费完成 ^_^");
                }
            };
            //自动回复队列应答 -- RabbitMQ中的消息确认机制,true: 为自动消费
            channel.basicConsume(queueName, true, consumer);
            log.info("rabbitmq初始化消费成功");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static abstract class ConsumerService {
        public abstract void consume(String queueName, String receiveMsg, String... args);
    }

}
