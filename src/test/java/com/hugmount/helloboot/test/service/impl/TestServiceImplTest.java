package com.hugmount.helloboot.test.service.impl;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author: lhm
 * @date: 2023/3/1
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestServiceImplTest {

    @Autowired
    private TestServiceImpl testService;

    @Test
    public void getTestList() {
        com.hugmount.helloboot.test.pojo.Test test = new com.hugmount.helloboot.test.pojo.Test();
        test.setId(1L);
        List<com.hugmount.helloboot.test.pojo.Test> testList = testService.getTestList(test);
        System.out.println(JSON.toJSONString(testList));
    }
}