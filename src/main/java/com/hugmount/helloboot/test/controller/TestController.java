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
import org.redisson.api.*;
import org.redisson.client.codec.IntegerCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    RedissonClient redissonClient;

    @RequestMapping("/testMongo")
    @ResponseBody
    public String testMongo() {
        List<User> users = mongoService.queryList("child.tel", "123", User.class);
        log.info(JSON.toJSONString(users));

        User user = mongoService.queryOne("id", "add123", User.class);
        log.info(JSON.toJSONString(user));

        User user1 = new User();
        user1.setId("add123");
        user1.setName("lihuiming");
        mongoService.save(user1);

        Map<String, Object> map = new HashMap<>();
        map.put("addr", "宋庄嘉华");
        mongoService.update("child.tel", "123", map, User.class);

        return "success";
    }

    @PostMapping("/getTestList")
    @ResponseBody
    public String getTestList(Test test) {
        log.info("getTestList: {}", JSON.toJSONString(test));
        List<Test> testList = testService.getTestList(test);
        String listStr = JSON.toJSONString(testList);
        System.out.println(listStr);
        return listStr;
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


    @ResponseBody
    @RequestMapping("/importExcel")
    public String importExcel(@RequestParam MultipartFile file) throws IOException {
        List<Map<String, Object>> maps = POIUtil.importExcel(file.getInputStream());
        log.info(JSON.toJSONString(maps));
        return "success";
    }

    @ResponseBody
    @RequestMapping("/testRedis")
    public String testRedis() {
        // 默认非公平锁
        // 非公平锁：多个线程去获取锁的时候，会直接去尝试获取，获取不到，再去进入等待队列，如果能获取到，就直接获取到锁
        // 优点：可以减少CPU唤醒线程的开销，整体的吞吐效率会高点
        RLock rLock = redissonClient.getLock("lock_key_" + "productId");
        try {
            rLock.lock(10, TimeUnit.SECONDS);
            log.info("执行任务中");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }

        // 公平锁
        // 多个线程按照申请锁的顺序去获得锁，线程会直接进入队列去排队，永远都是队列的第一位才能得到锁
        // 优点 所有的线程都能得到资源，不会饿死在队列中
        // 缺点 吞吐量会下降很多，队列里面除了第一个线程，其他的线程都会阻塞，cpu唤醒阻塞线程的开销会很大
        RLock key = redissonClient.getFairLock("key");
        return "success";
    }


    /**
     * 读写锁
     * 一次只有一个线程可以占有写模式 的读写锁, 但是可以有多个线程同时占有读模式 的读写锁
     * 读写锁适合对数据结构的读大于写很多的情况. 因为, 读模式锁定时可以共享, 以写模式锁住时意味着独占, 所以读写锁又叫共享-独占锁.
     * 无论是读请求先执行还是写请求先执行，只要涉及到写锁，则都会阻塞，如果是先写再读，则读锁等待，如果是先读再写，则写锁等待
     *
     * @return
     */
    @GetMapping("/read")
    @ResponseBody
    public String read() {
        RReadWriteLock orderId = redissonClient.getReadWriteLock("orderId");
        RLock rLock = orderId.readLock();
        try {
            rLock.lock();
            RBucket<Object> bucket = redissonClient.getBucket("lhm");
            Object obj = bucket.get();
            // 模拟耗时
//            Thread.sleep(9000);
            return obj.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }
        return "error";
    }


    @GetMapping("/write/{name}")
    @ResponseBody
    public String write(@PathVariable("name") String name) {
        RReadWriteLock orderId = redissonClient.getReadWriteLock("orderId");
        RLock rLock = orderId.writeLock();
        try {
            int incr = getIncr();
            log.info("生成分布式ID为: {}", incr);

            rLock.lock();
            RBucket<Object> bucket = redissonClient.getBucket("lhm");
            bucket.set(name);
            // 模拟耗时
            Thread.sleep(9000);
            return name;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }
        return "error";
    }

    public Integer getIncr() {
        RMapCache<Object, Integer> order = redissonClient.getMapCache("order_name_space", IntegerCodec.INSTANCE);
        // 业务号段的key
        String seqKey = "order_seq";
        // 过期时间, 0表示永不过期
        int timeout = 0;
        order.putIfAbsent(seqKey, 0, timeout, TimeUnit.DAYS);
        // 加1并获取计算后的值
        Integer id = order.addAndGet(seqKey, 1);
        return id;
    }

}
