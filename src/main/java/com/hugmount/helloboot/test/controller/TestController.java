package com.hugmount.helloboot.test.controller;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.hugmount.helloboot.core.Result;
import com.hugmount.helloboot.test.ChannelConfig;
import com.hugmount.helloboot.test.pojo.Test;
import com.hugmount.helloboot.test.service.TestService;
import com.hugmount.helloboot.util.POIUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

/**
 * @Author: Li Huiming
 * @Date: 2019/3/11
 */

@Controller
@RequestMapping
@Slf4j
public class TestController {

    @Value("${lhm}")
    private String testApollo;

    @Autowired
    TestService testService;

    @Autowired
    private ChannelConfig channelConfig;

    @PostMapping("/getTestList")
    @ResponseBody
    public String getTestList(@RequestBody @Valid Test test) {
        log.info("getTestList: {}", JSON.toJSONString(test));
        List<Test> testList = testService.getTestList(test);
        String listStr = JSON.toJSONString(testList);
        System.out.println(listStr);
        return listStr;
    }

    @PostMapping("/testValid")
    @ResponseBody
    public String testValid(@RequestBody @Validated(Test.QueryGroup.class) Test test) {
        log.info("testValid: {}", JSON.toJSONString(test));
        return "success";
    }

    @PostMapping("/batchTest")
    @ResponseBody
    public String batchTest() {
        Long batch = testService.batch();
        testService.insertTest();
        log.info("批量执行耗时ms: {}", batch);
        return "success";
    }

    /**
     * 默认主页
     *
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
    public Result<Date> getCurDate(Date curDate) {
        log.info(DateUtil.format(curDate, "yyyy-MM-dd HH:mm:ss"));
        log.info("测试apollo: {}", testApollo);
        Set<String> channel = channelConfig.getChannel();
        log.info("获取配置: {}", JSON.toJSONString(channel));
        return Result.createBySuccess("成功", new Date());
    }


    @GetMapping("/downExcel")
    public void downExcel(HttpServletResponse response) {
        log.info("begin");
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        linkedHashMap.put("name", "姓名");
        linkedHashMap.put("idCard", "身份证");
        linkedHashMap.put("amount", "金额");

        List<Map<String, Object>> list = new ArrayList<>();
        LinkedHashMap<String, Object> linkedHashMap1 = new LinkedHashMap();
        linkedHashMap1.put("name", "李");
        linkedHashMap1.put("idCard", "420622199601011235");
        linkedHashMap1.put("amount", "123456789.1234");
        list.add(linkedHashMap1);
        LinkedHashMap<String, Object> linkedHashMap2 = new LinkedHashMap();
        linkedHashMap2.put("name", "张");
        linkedHashMap2.put("idCard", "420622199601011236");
        linkedHashMap2.put("amount", "123456789.1234");
        list.add(linkedHashMap2);

        SXSSFWorkbook workbook = POIUtil.exportExcel(linkedHashMap, list);
        POIUtil.downloadExcel(workbook, response, "test.xlsx");
    }


    @ResponseBody
    @RequestMapping("/importExcel")
    public String importExcel(@RequestParam MultipartFile file) throws IOException {
        List<Map<String, Object>> maps = POIUtil.importExcel(file.getInputStream());
        log.info(JSON.toJSONString(maps));
        return "success";
    }


}
