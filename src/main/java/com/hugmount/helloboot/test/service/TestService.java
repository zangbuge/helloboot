package com.hugmount.helloboot.test.service;

import com.hugmount.helloboot.test.pojo.Test;

import java.util.List;

/**
 * @Author: Li Huiming
 * @Date: 2019/3/11
 */
public interface TestService {
    List<Test> getTestList(Test test);
}
