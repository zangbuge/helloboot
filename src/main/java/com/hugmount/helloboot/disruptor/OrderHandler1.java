package com.hugmount.helloboot.disruptor;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

/**
 * EventHandler用于EventHandlerGroup，WorkHandler用于WorkPool。
 * 同时实现两接口，该类对象可同时用于EventHandlerGroup和WorkPool
 *
 * @author lhm
 * @date 2024/1/24
 */
public class OrderHandler1 implements EventHandler<Order>, WorkHandler<Order> {

    private String consumerId;

    public OrderHandler1(String consumerId) {
        this.consumerId = consumerId;
    }

    /**
     * EventHandler
     *
     * @param order
     * @param sequence
     * @param endOfBatch
     */
    @Override
    public void onEvent(Order order, long sequence, boolean endOfBatch) {
        System.out.println("EventHandler消费消息: " + order.getId() + " consumerId" + consumerId);
    }

    /**
     * WorkHandler
     *
     * @param order
     */
    @Override
    public void onEvent(Order order) {
        System.out.println("WorkHandler消费消息: " + order.getId() + " consumerId" + consumerId);
    }
}
