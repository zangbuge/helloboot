package com.hugmount.helloboot.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * @author lhm
 * @date 2024/1/24
 */
public class OrderFactory implements EventFactory<Order> {
    @Override
    public Order newInstance() {
        return new Order();
    }
}
