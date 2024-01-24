package com.hugmount.helloboot.disruptor;

import com.lmax.disruptor.RingBuffer;

/**
 * @author lhm
 * @date 2024/1/24
 */
public class Producer {

    private final RingBuffer<Order> ringBuffer;

    public Producer(RingBuffer<Order> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    /**
     * 生产消息
     *
     * @param data
     */
    public void onData(String data) {
        long sequence = ringBuffer.next();
        try {
            Order order = ringBuffer.get(sequence);
            order.setId(data);
        } finally {
            ringBuffer.publish(sequence);
        }
    }
}
