package com.hugmount.helloboot.test.controller;

import com.alibaba.fastjson.JSON;
import com.hugmount.helloboot.core.Result;
import com.hugmount.helloboot.mongo.MongoService;
import com.hugmount.helloboot.mongo.User;
import com.hugmount.helloboot.test.pojo.Test;
import com.hugmount.helloboot.test.service.TestService;
import com.hugmount.helloboot.util.POIUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
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

    @Autowired
    MongoService mongoService;

    @RequestMapping("/testMongo")
    @ResponseBody
    public String testMongo() {
        List<User> users = mongoService.queryList("child.tel", "123", User.class);
        log.info(JSON.toJSONString(users));

        User user = mongoService.queryOne("id", "add123", User.class);
        log.info(JSON.toJSONString(user));
        user.setName("lihuiming");
        mongoService.save(user);

        Map<String, Object> map = new HashMap<>();
        map.put("addr", "宋庄嘉华");
        mongoService.update("child.tel", "123", map, User.class);

        return "success";
    }

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
        map.put("name", "LHM");
        map.put("today", new Date());
        List<String> list = new LinkedList<>();
        list.add("北京");
        list.add("武汉");
        list.add("上海");
        map.put("city", list);
        modelMap.addAttribute("res", map);
        return "test";
    }


    @ApiOperation("获取当前时间")
    @RequestMapping("/getCurDate")
    @ResponseBody
    public Result<Date> getCurDate() {
        return Result.createBySuccess("成功", new Date());
    }


    @GetMapping("/downExcel")
    public void downExcel(HttpServletResponse response) {
        log.info("begin");
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        linkedHashMap.put("name", "姓名");
        linkedHashMap.put("age", "年龄");
        linkedHashMap.put("sex", "性别");

        List<Map<String, Object>> list = new ArrayList<>();
        LinkedHashMap<String, Object> linkedHashMap1 = new LinkedHashMap();
        linkedHashMap1.put("name", "李");
        linkedHashMap1.put("sex", "男");
        linkedHashMap1.put("age", "12");
        list.add(linkedHashMap1);
        LinkedHashMap<String, Object> linkedHashMap2 = new LinkedHashMap();
        linkedHashMap2.put("name", "张");
        linkedHashMap2.put("age", "23");
        linkedHashMap2.put("sex", "女");
        list.add(linkedHashMap2);

        SXSSFWorkbook workbook = POIUtil.exportExcel(linkedHashMap, list);
        POIUtil.downloadExcel(workbook, response, "test.xlsx");
    }

}
