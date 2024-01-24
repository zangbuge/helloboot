package com.hugmount.helloboot.disruptor;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.Executors;

/**
 * Disruptor 是一个并发组件,能够在无锁(实际使用CAS)的情况下实现Queue并发安全操作,数组实现,环形队列
 * 作用与 ArrayBlockingQueue 相似,但功能,性能更优
 * 等待策略:
 * BusySpinWaitStrategy: 自旋等待, 低延迟但同时对CPU资源的占用也多
 * BlockingWaitStrategy:  使用锁和条件变量, CPU资源的占用少，延迟大
 * SleepingWaitStrategy: 在多次循环尝试不成功后,选择让出CPU. 等待下次调度,多次调度后仍不成功,尝试前睡眠一个纳秒级别的时间再尝试
 * YieldingWaitStrategy: 充分压榨 CPU 的策略(慎用),使用自旋+yield的方式来提高性能
 * PhasedBackoffWaitStrategy: 上面多种策略的综合,CPU资源的占用少,延迟大
 * 消费方式:
 * handleEventsWith: 返回的EventHandlerGroup, Group中的每个消费者都会对m进行消费，各个消费者之间不存在竞争
 * handleEventsWithWorkerPool: 返回的EventHandlerGroup, Group的消费者对于同一条消息m不重复消费
 *
 * @author lhm
 * @date 2024/1/24
 */
public class DisruptorTest {

    // 单生产者单消费者
    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();
        // 不要太大,否则会造成oom
        int bufferSize = 1024 * 1024;
        // 构造缓冲区与事件生成, Disruptor交给线程池来处理，共计 p1,c1,c2,c3四个线程
        Disruptor<Order> disruptor = new Disruptor<>(new OrderFactory(), bufferSize, Executors.defaultThreadFactory(), ProducerType.SINGLE, new YieldingWaitStrategy());
        disruptor.handleEventsWith(new OrderHandler1("consumer1"));
        disruptor.start();
        RingBuffer<Order> ringBuffer = disruptor.getRingBuffer();
        Producer producer = new Producer(ringBuffer);
        //单生产者，生产3条数据
        for (int l = 0; l < 3; l++) {
            producer.onData(l + "");
        }
        //为了保证消费者线程已经启动，留足足够的时间. 在调用disruptor.shutdown方法前，所有的消费者线程都已经启动
        Thread.sleep(1000);
        disruptor.shutdown();
    }
}
