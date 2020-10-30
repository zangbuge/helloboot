package com.hugmount.helloboot.util;

import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @Author: Li Huiming
 * @Date: 2019/5/15
 */

@Slf4j
public class RabbitmqUtil {

    private static Connection connection = null;

    private static final String DEFAULT_ROUTER = "info";

    // RabbitMQ有四种交换机类型:
    // 1. direct(默认) : 处理routingKey。通过routingKey和exchange完全匹配的那个唯一的queue才可以接收消息。可持久化.
    // 2. fanout : 广播模式不处理路由键。所有bind到此exchange的queue都可以接收消息, Fanout交换机转发消息是最快的,
    // 若发送的时刻没有对端接收，那消息就没了，因此在广播模式下设置消息持久化是无效的。
    // 3. topic: 将路由键和某模式进行模糊匹配。此时队列需要绑定要一个模式上。
    // 符号“#”匹配一个或多个词，符号“*”匹配不多不少一个词。因此“abc.#”能够匹配到“abc.def.ghi”，但是“abc.*” 只会匹配到“abc.def”
    // 4. headers : 不处理路由键。而是根据发送的消息内容中的headers属性进行匹配。在绑定Queue与Exchange时指定一组键值对；
    // 当消息发送到RabbitMQ时会取到该消息的headers与Exchange绑定时指定的键值对进行匹配；如果完全匹配则消息会路由到该队列，
    // 否则不会路由到该队列。headers属性是一个键值对，可以是Hashtable，键值对的值可以是任何类型
    private static final String EXCHANGE_TYPE = "direct";

    public static Connection createConnection(String ip, int port, String username, String password, String vHost) {
        if (null != connection) {
            log.info("rabbitmq init already");
            return connection;
        }

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(ip);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(vHost);
        // 网络故障自动连接恢复
        connectionFactory.setAutomaticRecoveryEnabled(true);
        try {
            connection = connectionFactory.newConnection();
            log.info("create rabbitmq connection success");
        } catch (Exception e) {
            log.error("create rabbitmq connection fail", e);
        }
        return connection;
    }


    public static void sendMsg(String exchangeName, String exchangeType, String queueName, String router, String msg) {
        sendMsg(connection, exchangeName, exchangeType, queueName, router, msg);
    }

    public static void sendMsg(Connection connection, String exchangeName, String exchangeType, String queueName, String router, String msg) {
        if (null == router || "".equals(router.trim())) {
            router = DEFAULT_ROUTER;
        }
        if (null == exchangeType || "".equals(exchangeType.trim())) {
            exchangeType = EXCHANGE_TYPE;
        }

        try {
            //创建一个通道
            Channel channel = connection.createChannel();
            //指定交换机类型
            channel.exchangeDeclare(exchangeName, exchangeType);
            //声明一个队列, 如果不存在则创建, true表示持久化
            channel.queueDeclare(queueName, true, false, false, null);
            //推送消息到队列中,并指定路由
            channel.basicPublish(exchangeName, router, null, msg.getBytes("UTF-8"));
            log.info("send msg to rabbitmq success");

            //关闭通道
            channel.close();
        } catch (Exception e) {
            log.error("send msg to rabbitmq fail", e);
            throw new RuntimeException(e);
        }
    }

    public static void receive(String exchangeName, String queueName, String router, final ConsumerService consumerService) {
        if (null == router || "".equals(router.trim())) {
            router = DEFAULT_ROUTER;
        }
        receive(connection, exchangeName, queueName, router, consumerService);
    }

    /**
     * 该方法只用初始化一次即可
     *
     * @param connection
     * @param queueName
     * @param consumerService
     */
    public static void receive(Connection connection, String exchangeName, String queueName, final String router, final ConsumerService consumerService) {
        log.info("rabbitmq consumer init start");
        try {
            //创建一个通道
            Channel channel = connection.createChannel();
            //订阅路由,可有多个订阅
            channel.queueBind(queueName, exchangeName, router);
            //声明要关注的队列
            channel.queueDeclare(queueName, true, false, false, null);
            //定义一个消费者DefaultConsumer, 如果通道中有消息,就会执行回调函数handleDelivery
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body)
                        throws IOException {

                    try {
                        log.info("rabbitmq consume start");
                        String msg = new String(body, "UTF-8");
                        consumerService.consume(msg, queueName, router);
                        // 消息确认
                        // deliveryTag:（唯一标识 ID） 它代表了 RabbitMQ 向该 Channel 投递的这条消息的唯一标识 ID
                        // 是一个单调递增的正整数，delivery tag 的范围仅限于 Channel
                        // multiple：为了减少网络流量，手动确认可以被批处理，当该参数为 true 时，则可以一次性确认
                        channel.basicAck(envelope.getDeliveryTag(), false);
                        log.info("rabbitmq consume success");
                    } catch (Exception e) {
                        log.error("rabbitmq consume fail please try again", e);
                    }

                }
            };

            // 设置消息确认机制, true: 为自动消费模式, false: 手动
            // 自动确认会在消息发送给消费者后立即确认，但存在丢失消息的可能
            channel.basicConsume(queueName, false, consumer);
            log.info("rabbitmq consumer init success");

        } catch (Exception e) {
            log.error("rabbitmq consumer init fail", e);
        }

    }

    public static abstract class ConsumerService {
        public abstract void consume(String receiveMsg, String... args);
    }

}
