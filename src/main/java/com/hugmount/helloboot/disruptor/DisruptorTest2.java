package com.hugmount.helloboot.disruptor;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.Executors;

/**
 * @author lhm
 * @date 2024/1/24
 */
public class DisruptorTest2 {
    // 单生产者，多消费者，但多消费者间形成依赖关系，每个依赖节点单线程
    // 如消息 m1 消费者C2 必须在C1消费完后才能消费m1, 以此类推
    public static void main(String[] args) throws InterruptedException {
        EventFactory<Order> factory = new OrderFactory();
        int ringBufferSize = 1024 * 1024;
        Disruptor<Order> disruptor = new Disruptor<>(factory, ringBufferSize, Executors.defaultThreadFactory(), ProducerType.SINGLE, new YieldingWaitStrategy());
        // 多个消费者间形成依赖关系，每个依赖节点的消费者为单线程。
        disruptor.handleEventsWith(new OrderHandler1("1")).then(new OrderHandler1("2"), new OrderHandler1("3")).then(new OrderHandler1("4"));
        disruptor.start();
        Producer producer = new Producer(disruptor.getRingBuffer());
        //单生产者，生产3条数据
        for (int l = 0; l < 3; l++) {
            producer.onData(l + "");
        }
        //为了保证消费者线程已经启动，留足足够的时间
        Thread.sleep(1000);
        disruptor.shutdown();
        System.out.println("触发完成");
    }
}
