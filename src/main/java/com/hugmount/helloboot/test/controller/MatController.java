package com.hugmount.helloboot.test.controller;

import com.hugmount.helloboot.core.Result;
import com.hugmount.helloboot.mongo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 环境: 设置jvm运行参数, 模拟内存溢出情况：-Xms128m -Xmx256m -XX:MaxPermSize=256m
 *
 * @author Li Huiming
 * @date 2022/3/16
 */
@Slf4j
@RestController
public class MatController {

    @GetMapping("testMat")
    public Result<String> testMat() {
        log.info("start mat");
        Map<String, Object> map = new HashMap<>();
        int i = 0;
        do {
            User user = new User();
            user.setName("lhm");
            map.put(String.valueOf(i), user);
            i++;
        } while (i < 999999999);
        log.info("成功");
        return Result.createBySuccess("成功");
    }

}
