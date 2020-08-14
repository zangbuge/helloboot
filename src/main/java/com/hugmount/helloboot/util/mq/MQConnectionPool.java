package com.hugmount.helloboot.util.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Author: Li Huiming
 * @Date: 2020/8/13
 */
@Slf4j
public class MQConnectionPool {

    private PoolProperties poolProperties;

    private static ConnectionFactory connectionFactory = new ConnectionFactory();

    private ConcurrentLinkedQueue<ConnectionStream> connectionStreams;

    private ConcurrentLinkedQueue<ChannelStream> channelStreams;

    public MQConnectionPool(PoolProperties poolProperties) {
        this.poolProperties = poolProperties;
        init();
    }

    // 释放连接
    public synchronized void removeConnectionStream(ConnectionStream connectionStream){
        try {
            connectionStream.getConnection().close();
        } catch (IOException e) {
            log.error("释放rabbitmq连接异常", e);
        }
    }

    // 空闲池获取连接
    public synchronized ConnectionStream getConnectionStream() {
        // 获取并移除队列的头，如果队列为空则返回null
        ConnectionStream poll = connectionStreams.poll();
        if (poll == null) {
            try {
                log.info("当前没有rabbitmq空闲连接, 等待3秒...");
                Thread.sleep(1000 * 3);
                return getConnectionStream();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        int useQuantity = poll.getUseQuantity();
        useQuantity += 1;
        poll.setUseQuantity(useQuantity);
        poll.setLastUseTime(System.currentTimeMillis());
        poll.setFree(false);
        return poll;
    }

    // 释放通道
    public synchronized void releaseChannelStream(ChannelStream channelStream){
        try {
            channelStream.setFree(true);
            int useQuantity = channelStream.getUseQuantity();
            useQuantity -= 1;
            channelStream.setUseQuantity(useQuantity);
            channelStreams.offer(channelStream);
        } catch (Exception e) {
            log.error("释放rabbitmq通道异常", e);
        }

    }

    // 获取通道
    public synchronized ChannelStream getChannelStream(){
        ChannelStream stream = channelStreams.poll();
        if (stream == null) {
            stream = new ChannelStream();
            Connection connection = getConnectionStream().getConnection();
            Channel channel = createChannel(connection);
            stream.setChannel(channel);
            stream.setUseQuantity(0);
        }
        int useQuantity = stream.getUseQuantity();
        useQuantity += 1;
        stream.setUseQuantity(useQuantity);
        stream.setLastUseTime(System.currentTimeMillis());
        stream.setFree(false);
        return stream;
    }

    private void init() {
        connectionStreams = new ConcurrentLinkedQueue<>();
        channelStreams = new ConcurrentLinkedQueue<>();

        int minConn = poolProperties.getMinConn();
        minConn = minConn > 0 ? minConn : 1;
        for (int i = 0; i < minConn; i++) {
            String url = poolProperties.getUrl();
            int port = poolProperties.getPort();
            String user = poolProperties.getUser();
            String password = poolProperties.getPassword();
            String vHost = poolProperties.getVHost();
            Connection connection = createConnection(url, port, user, password, vHost);
            ConnectionStream connectionStream = new ConnectionStream();
            connectionStream.setConnection(connection);
            connectionStream.setFree(true);
            connectionStream.setUseQuantity(0);
            connectionStreams.offer(connectionStream);
        }
    }

    private Connection createConnection(String ip, int port, String username, String password, String vHost) {
        connectionFactory.setHost(ip);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(vHost);
        // 网络故障自动连接恢复
        connectionFactory.setAutomaticRecoveryEnabled(true);
        Connection connection = null;
        try {
            connection = connectionFactory.newConnection();
            log.info("create rabbitmq connection success");
        } catch (Exception e) {
            log.error("create rabbitmq connection fail", e);
        }
        return connection;
    }

    private Channel createChannel(Connection connection) {
        try {
            Channel channel = connection.createChannel();
            log.info("rabbitmq create channel success");
            return channel;
        } catch (Exception e) {
            log.error("rabbitmq create channel fail", e);
            throw new RuntimeException(e);
        }
    }


    @Data
    public static class ConnectionStream {
        private Connection connection;
        private boolean free;
        private int useQuantity;
        private Long lastUseTime;
    }

    @Data
    public static class ChannelStream {
        private Channel channel;
        private boolean free;
        private int useQuantity;
        private Long lastUseTime;
    }

}
