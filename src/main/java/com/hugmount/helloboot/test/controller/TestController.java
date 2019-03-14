package com.hugmount.helloboot.test.controller;

import com.alibaba.fastjson.JSON;
import com.hugmount.helloboot.test.pojo.Test;
import com.hugmount.helloboot.test.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: Li Huiming
 * @Date: 2019/3/11
 */

@RestController
@RequestMapping("/testController")
public class TestController {

    @Autowired
    TestService testService;


    @PostMapping("/getTestList")
    public String getTestList(Test test){
        List<Test> testList = testService.getTestList(test);
        String listStr = JSON.toJSONString(testList);
        System.out.println(listStr);
        return listStr;
    }
}
