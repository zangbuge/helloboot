package com.hugmount.helloboot.disruptor;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.Executors;

/**
 * @author lhm
 * @date 2024/1/25
 */
public class DisruptorTest4 {
    //单生产者，多消费者模式。多消费者对于消息独立消费。例如：对于消息m，两个消费者都要对其进行消费。
    public static void main(String[] args) throws Exception {
        EventFactory<Order> factory = new OrderFactory();
        int ringBufferSize = 1024 * 1024;
        Disruptor<Order> disruptor =
                new Disruptor<>(factory, ringBufferSize, Executors.defaultThreadFactory(), ProducerType.SINGLE, new YieldingWaitStrategy());
        /*
         * 两个消费者创建EventHandlerGroup。该消费者需要实现EventHandler类。两个消费者对于RingBuffer中的每个消息，都独立消费一次。
         * 两个消费者在消费消息的过程中，各自独立，不产生竞争。
         */
        disruptor.handleEventsWith(new OrderHandler1("1"), new OrderHandler1("2"));
        disruptor.start();
        RingBuffer<Order> ringBuffer = disruptor.getRingBuffer();
        Producer producer = new Producer(ringBuffer);
        //单生产者
        for (int l = 0; l < 5; l++) {
            producer.onData(l + "");
        }
        //为了保证消费者线程已经启动，留足足够的时间。
        Thread.sleep(1000);
        disruptor.shutdown();
    }
}
