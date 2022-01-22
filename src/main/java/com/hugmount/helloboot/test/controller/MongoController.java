package com.hugmount.helloboot.test.controller;

import com.alibaba.fastjson.JSON;
import com.hugmount.helloboot.mongo.MongoService;
import com.hugmount.helloboot.mongo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Profile("prod")
@RestController
public class MongoController {

    @Autowired
    MongoService mongoService;

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

}
