package com.hugmount.helloboot.test.service.impl;

import com.github.pagehelper.PageHelper;
import com.hugmount.helloboot.test.mapper.TestMapper;
import com.hugmount.helloboot.test.pojo.Test;
import com.hugmount.helloboot.test.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: Li Huiming
 * @Date: 2019/3/11
 */
@Service("testService")
public class TestServiceImpl implements TestService {


    @Autowired
    private TestMapper testMapper;

    @Override
    public List<Test> getTestList(Test test) {
        PageHelper.startPage(2,3);
        return testMapper.getTestList(test);
    }
}
