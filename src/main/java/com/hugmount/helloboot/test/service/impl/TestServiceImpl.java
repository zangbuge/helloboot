package com.hugmount.helloboot.test.service.impl;

import com.github.pagehelper.PageHelper;
import com.hugmount.helloboot.test.mapper.TestMapper;
import com.hugmount.helloboot.test.pojo.Test;
import com.hugmount.helloboot.test.service.TestService;
import com.hugmount.helloboot.util.SqlSessionFactoryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Li Huiming
 * @Date: 2019/3/11
 */

@Slf4j
@Service("testService")
public class TestServiceImpl implements TestService {

    @Autowired
    private SqlSessionFactoryUtil sqlSessionFactoryUtil;

    @Autowired
    private TestMapper testMapper;

    @Override
    public List<Test> getTestList(Test test) {
        PageHelper.startPage(1, 3);
        return testMapper.getTestList(test);
    }

    public void batch() {
        List<Test> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Test test = new Test();
            test.setUsername("lhm" + i);
            list.add(test);
        }
        long start = System.currentTimeMillis();
        log.info("开始执行: {}", start);
        sqlSessionFactoryUtil.batch(list, testMapper::insert);
        long use = System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        log.info("开始执行: {}", start);
        for (Test test : list) {
            testMapper.insert(test);
        }
        log.info("单条执行完成耗时ms: {}", System.currentTimeMillis() - start);
        log.info("批量执行完成耗时ms: {}", use);
    }
}
