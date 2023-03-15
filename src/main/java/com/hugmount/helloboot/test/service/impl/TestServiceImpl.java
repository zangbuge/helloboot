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
        for (int i = 0; i < 10000; i++) {
            Test test = new Test();
            test.setUsername("lhm" + i);
            list.add(test);
        }
        log.info("开始执行");
        sqlSessionFactoryUtil.batch(list, TestMapper.class, testMapper::insert);
        log.info("执行完成");
    }
}
