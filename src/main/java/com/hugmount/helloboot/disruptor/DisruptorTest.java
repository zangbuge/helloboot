package com.hugmount.helloboot.disruptor;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.Executors;

/**
 * Disruptor 是一个并发组件,能够在无锁(实际使用CAS)的情况下实现Queue并发安全操作,数组实现,环形队列
 * 作用与 ArrayBlockingQueue 相似,但功能,性能更优. 官方对比数据: https://lmax-exchange.github.io/disruptor/disruptor.html#_throughput_performance_testing
 * 线程安全性:
 * Disruptor使用序列号(Sequence)来标识RingBuffer中的位置，消费者通过跟踪这个序列号来获取要消费的数据
 * 消费者只需要关注自己的序列号，不会干扰其他消费者，从而实现了线程安全
 * 等待策略:
 * BusySpinWaitStrategy: 自旋等待, 低延迟但同时对CPU资源的占用也多
 * BlockingWaitStrategy:  使用锁和条件变量, CPU资源的占用少，延迟大
 * SleepingWaitStrategy: 在多次循环尝试不成功后,选择让出CPU. 等待下次调度,多次调度后仍不成功,尝试前睡眠一个纳秒级别的时间再尝试
 * YieldingWaitStrategy: 充分压榨 CPU 的策略(慎用),使用自旋+yield的方式来提高性能
 * PhasedBackoffWaitStrategy: 上面多种策略的综合,CPU资源的占用少,延迟大
 * 消费方式:
 * handleEventsWith: 返回的EventHandlerGroup, Group中的每个消费者都会对m进行消费，各个消费者之间不存在竞争
 * handleEventsWithWorkerPool: 返回的EventHandlerGroup, Group的消费者对于同一条消息m不重复消费
 * springboot中使用disruptor:
 * 定义一个MQManager配置类, 启动disruptor, 注入 RingBuffer bean. 注入 Producer Service, 发送消息
 * 环形队列原理: 环形缓冲区的核心精华在于对读写指针移动进行取模求余运算
 * 数组首位相连,使用Sequence对ringBufferSize取模求余,计算出当前的位置
 * 定义了两个指针, 一个写指针，一个读指针
 * 读指针指向环形缓冲区可读数据的第一个数据地址
 * 写指针指向环形环形缓冲区可写数据的第一个数据地址
 * 写操作时，需要先进行判断环形缓冲区是否已写满，若已写满，直接覆盖数据；或做相应的处理
 * 读操作时，需要进行判断环形缓冲区是否为空，若为空则无法读取数据
 * read_index = (read_index + 1) % ringBufferSize
 * write_index = (write_index + 1) % ringBufferSize
 * read_index: 当前读的位置
 * write_index: 当前写的位置
 * ringBufferSize: 缓冲区大小
 * 当 read_index = write_index 时, 说明环形缓冲区为空
 * 当（(write_index + 1）% ringBufferSize) = read_index，说明环形缓冲区已满
 * 若有多个任务需要读写环形缓冲区时，必须添加互斥保护机制，确保每个任务均正确访问环形缓冲区
 *
 * @author lhm
 * @date 2024/1/24
 */
public class DisruptorTest {

    // 单生产者单消费者
    public static void main(String[] args) throws InterruptedException {
        // 必须是 2 的 n 次方, 不要太大,否则会造成oom
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
