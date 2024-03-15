package com.hugmount.helloboot.test.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hugmount.helloboot.test.pojo.TTest;
import com.hugmount.helloboot.test.pojo.Test;

import java.util.List;

/**
 * @Author: Li Huiming
 * @Date: 2019/3/11
 */
public interface TestService extends IService<Test> {
    List<Test> getTestList(Test test);

    Long batch();

    void insertTest();

    long mybatisPlusBatch();

    Long batchAndThread();

    List<TTest> getTTestList(TTest tTest);

    void updateData(TTest tTest);

}
