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

    private static final String DEFAULT_ROUTER = "info";

    // RabbitMQ有四种交换机类型:
    // 1. direct : (默认)处理路由键。需要将一个队列绑定到交换机上，要求该消息与一个特定的路由键完全匹配
    // 2. fanout : 不处理路由键。你只需要简单的将队列绑定到交换机上。Fanout交换机转发消息是最快的
    // 一个发送到交换机的消息都会被转发到与该交换机绑定的所有队列上。很像子网广播，每台子网内的主机都获得了一份复制的消
    // 3. topic: 将路由键和某模式进行匹配。此时队列需要绑定要一个模式上。
    // 符号“#”匹配一个或多个词，符号“”匹配不多不少一个词。因此“abc.#”能够匹配到“abc.def.ghi”，但是“abc.” 只会匹配到“abc.def”
    // 4. headers : 不处理路由键。而是根据发送的消息内容中的headers属性进行匹配。在绑定Queue与Exchange时指定一组键值对；
    // 当消息发送到RabbitMQ时会取到该消息的headers与Exchange绑定时指定的键值对进行匹配；如果完全匹配则消息会路由到该队列，
    // 否则不会路由到该队列。headers属性是一个键值对，可以是Hashtable，键值对的值可以是任何类型
    private static final String EXCHANGE_TYPE = "direct";

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


    public static void sendMsg (String exchangeName, String queueName, String router, String msg) {
        sendMsg(connection, exchangeName, queueName, router, msg);
    }

    public static void sendMsg (Connection connection, String exchangeName, String queueName, String router, String msg) {
        if (null == router || "" == router.trim()) {
            router = DEFAULT_ROUTER;
        }
        try {
            //创建一个通道
            Channel channel = connection.createChannel();
            //指定交换机类型
            channel.exchangeDeclare(exchangeName ,EXCHANGE_TYPE);
            //声明一个队列, 如果不存在则创建, true表示持久化
            channel.queueDeclare(queueName,true, false, false, null);
            //推送消息到队列中,并指定路由
            channel.basicPublish(exchangeName ,router ,null, msg.getBytes("UTF-8"));
            log.info("消息推送到 rabbitmq success !");

            //关闭通道
//            channel.close();
//            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void receive (String exchangeName, String queueName, String router, final ConsumerService consumerService) {
        if (null == router || "" == router.trim()) {
            router = DEFAULT_ROUTER;
        }
        receive(connection, exchangeName, queueName, router, consumerService);
    }

    /**
     * 该方法只用初始化一次即可
     * @param connection
     * @param queueName
     * @param consumerService
     */
    public static void receive (Connection connection, String exchangeName, String queueName, final String router, final ConsumerService consumerService) {
        log.info("rabbitmq初始化消费...");
        try {
            //创建一个通道
            Channel channel = connection.createChannel();
            //订阅路由,可有多个订阅
            channel.queueBind(queueName, exchangeName, router);
            //声明要关注的队列
            channel.queueDeclare(queueName,true, false, false, null);
            //定义一个消费者DefaultConsumer, 如果通道中有消息,就会执行回调函数handleDelivery
            Consumer consumer = new DefaultConsumer(channel) {
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body)
                        throws IOException {
                    log.info("rabbitmq开始消费...");
                    String msg = new String(body, "UTF-8");
                    consumerService.consume(msg, queueName ,router);
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
        public abstract void consume(String receiveMsg, String... args);
    }

}