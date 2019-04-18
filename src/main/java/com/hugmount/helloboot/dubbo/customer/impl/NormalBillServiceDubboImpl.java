package com.hugmount.helloboot.dubbo.customer.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.hugmount.helloboot.dubbo.customer.NormalBillServiceDubbo;

/**
 * @Author: Li Huiming
 * @Date: 2019/4/18
 */
@Service
public class NormalBillServiceDubboImpl implements NormalBillServiceDubbo {

    /**
     * 财务工资驳回
     *
     * @param salaryId
     * @return
     */
    @Override
    public String rejectSalary(String salaryId) {
        return "sucess";
    }
}
