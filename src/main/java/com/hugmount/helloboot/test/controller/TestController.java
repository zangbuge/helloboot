package com.hugmount.helloboot.test.controller;

import com.alibaba.fastjson.JSON;
import com.hugmount.helloboot.test.pojo.Test;
import com.hugmount.helloboot.test.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

/**
 * @Author: Li Huiming
 * @Date: 2019/3/11
 */

@Controller
@RequestMapping
@Slf4j
public class TestController {

    @Autowired
    TestService testService;


    @PostMapping("/getTestList")
    @ResponseBody
    public String getTestList(Test test){
        log.info("getTestList: {}", JSON.toJSONString(test));
        List<Test> testList = testService.getTestList(test);
        String listStr = JSON.toJSONString(testList);
        System.out.println(listStr);
        return listStr;
    }

    /**
     * 默认主页
     * @return
     */
    @RequestMapping("/")
    public ModelAndView index() {
        log.info("进入主页");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");
        Map<String, Object> map = new HashMap<>();
        map.put("name", "lhm");
        map.put("today", new Date());
        List<String> list = new LinkedList<>();
        list.add("北京");
        list.add("武汉");
        list.add("襄阳");
        map.put("city", list);
        modelAndView.addObject("res", map);
        return modelAndView;
    }

    @RequestMapping("/test")
    public String test(ModelMap modelMap) {
        log.info("进入测试 test");
        Map<String, Object> map = new HashMap<>();
        map.put("name", "lhm");
        map.put("today", new Date());
        List<String> list = new LinkedList<>();
        list.add("北京");
        list.add("武汉");
        list.add("襄阳");
        map.put("city", list);
        modelMap.addAttribute("res", map);
        return "test";
    }

}
