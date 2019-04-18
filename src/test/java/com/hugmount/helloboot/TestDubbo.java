package com.hugmount.helloboot;

import com.alibaba.dubbo.config.annotation.Reference;
import com.hugmount.helloboot.dubbo.customer.NormalBillServiceDubbo;
import org.junit.Test;

/**
 * @Author: Li Huiming
 * @Date: 2019/4/18
 */
public class TestDubbo extends HellobootApplicationTests {

    @Reference
    NormalBillServiceDubbo normalBillServiceDubbo;

    @Test
    public void testCustomer(){
        String result = normalBillServiceDubbo.rejectSalary("123");
        System.out.println("调用dubbo服务返回: " + result);
    }
}
