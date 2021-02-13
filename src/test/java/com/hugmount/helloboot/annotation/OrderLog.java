package com.hugmount.helloboot.annotation;

/**
 * @Author: Li Huiming
 * @Date: 2019/6/14
 */
public class OrderLog extends LogAspect {

    private int orderId = 123;

    public String date = "2019-02-30";

    private void helloPrivate (int i){}

    public String sendOrder(String id) {
        return "下单成功";
    }

}
