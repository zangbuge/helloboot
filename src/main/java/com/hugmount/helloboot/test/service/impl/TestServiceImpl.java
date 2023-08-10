package com.hugmount.helloboot.test.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.hugmount.helloboot.test.mapper.TestMapper;
import com.hugmount.helloboot.test.pojo.Test;
import com.hugmount.helloboot.test.service.TestService;
import com.hugmount.helloboot.util.SqlSessionFactoryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Li Huiming
 * @Date: 2019/3/11
 */

@Slf4j
@Service("testService")
public class TestServiceImpl extends ServiceImpl<TestMapper, Test> implements TestService {

    @Autowired
    private SqlSessionFactoryUtil sqlSessionFactoryUtil;

    @Autowired
    private TestMapper testMapper;

    @Override
    public List<Test> getTestList(Test test) {
        PageHelper.startPage(1, 3);
        return testMapper.getTestList(test);
    }

    @Transactional
    public Long batch() {
        List<Test> list = getList();
        long start = System.currentTimeMillis();
        log.info("批量执行开始: {}", start);
        sqlSessionFactoryUtil.batch(list, testMapper::insertTest);
        long use = System.currentTimeMillis() - start;
        log.info("批量执行完成耗时ms: {}", use);
        return use;
    }

    @Transactional
    public void insertTest() {
        List<Test> list = getList();
        long start = System.currentTimeMillis();
        log.info("单条执行开始: {}", start);
        for (Test test : list) {
            testMapper.insert(test);
        }
        log.info("单条执行完成耗时ms: {}", System.currentTimeMillis() - start);
    }

    @Transactional
    public long mybatisPlusBatch() {
        long start = System.currentTimeMillis();
        log.info("mybatisPlusBatch: {}", start);
        this.saveBatch(getList());
        long use = System.currentTimeMillis() - start;
        log.info("mybatisPlusBatch完成耗时ms: {}", use);
        return use;
    }


    List<Test> getList() {
        List<Test> list = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            Test test = new Test();
            test.setUsername("lhm" + i);
            list.add(test);
        }
        return list;
    }
}
