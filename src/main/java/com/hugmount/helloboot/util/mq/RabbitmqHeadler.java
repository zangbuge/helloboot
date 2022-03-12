package com.hugmount.helloboot.util.mq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @Author: Li Huiming
 * @Date: 2020/8/13
 */
@Slf4j
public class RabbitmqHeadler {

    private static MQConnectionPool mqConnectionPool;

    public static void init(PoolProperties poolProperties) {
        if (mqConnectionPool == null) {
            mqConnectionPool = new MQConnectionPool(poolProperties);
        }
    }

    public static synchronized void sendMsg(String exchangeName, String exchangeType,  String queueName, String router, String msg) {
        MQConnectionPool.ChannelStream channelStream = mqConnectionPool.getChannelStream();
        Channel channel = channelStream.getChannel();
        try {
            //指定交换机类型
            channel.exchangeDeclare(exchangeName ,exchangeType);
            //声明一个队列, 如果不存在则创建, true表示持久化
            channel.queueDeclare(queueName,true, false, false, null);
            // 推送消息到队列中,并指定路由
            channel.basicPublish(exchangeName ,router ,null, msg.getBytes("UTF-8"));
            mqConnectionPool.releaseChannelStream(channelStream);
            log.info("send msg to rabbitmq success");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
