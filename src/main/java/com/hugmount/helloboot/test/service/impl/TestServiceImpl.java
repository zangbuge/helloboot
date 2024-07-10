package com.hugmount.helloboot.test.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.hugmount.helloboot.test.mapper.TTestMapper;
import com.hugmount.helloboot.test.mapper.TestMapper;
import com.hugmount.helloboot.test.pojo.TTest;
import com.hugmount.helloboot.test.pojo.Test;
import com.hugmount.helloboot.test.service.TestService;
import com.hugmount.helloboot.util.SqlSessionFactoryUtil;
import com.hugmount.helloboot.util.StrUtil;
import com.hugmount.helloboot.util.ThreadUtil;
import com.hugmount.helloboot.util.TransactionManagerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private TransactionManagerUtil transactionManagerUtil;

    @Autowired
    private TTestMapper tTestMapper;

    @Override
    public List<Test> getTestList(Test test) {
        PageHelper.startPage(1, 3);
        return testMapper.getTestList(test);
    }

    @Override
    public List<TTest> getTTestList(TTest tTest) {
        List<TTest> tests = tTestMapper.selectList(Wrappers.<TTest>lambdaQuery().eq(TTest::getId, tTest.getId()));
        log.info("getTTestList数据: {}", JSONUtil.toJsonStr(tests));
        return tests;
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

    /**
     * 多线程批量执行,1万条数据耗时1.2秒
     *
     * @return
     */
    @Transactional
    public Long batchAndThread() {
        List<Test> list = getList();
        long start = System.currentTimeMillis();
        List<TransactionManagerUtil.Task> tasks = new CopyOnWriteArrayList<>();
        List<List<Test>> split = CollUtil.split(list, 2000);
        split.forEach(it -> {
            tasks.add(() -> {
                sqlSessionFactoryUtil.batch(it, testMapper::insertTest);
            });
        });
        transactionManagerUtil.execute(tasks, ThreadUtil.getExecutorService(), 8, TimeUnit.SECONDS);
        long use = System.currentTimeMillis() - start;
        log.info("batchAndThread完成耗时ms: {}", use);
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

    /**
     * 更新不忽略空, 覆盖更新
     *
     * @param tTest
     */
    @Override
    public void updateData(TTest tTest) {
        tTestMapper.updateById(tTest);
    }

    public String testStrUtil(String url) {
        String key = StrUtil.getUrlParam(url, "name");
        System.out.println("getUrlParam name: " + key);
        String baseUrl = StrUtil.getUrlAddr(url);
        return baseUrl;
    }

}
