package com.hugmount.helloboot.thrift.server.impl;

import com.alibaba.fastjson.JSON;
import com.hugmount.helloboot.test.pojo.Test;
import com.hugmount.helloboot.test.service.impl.TestServiceImpl;
import com.hugmount.helloboot.thrift.server.HelloService;
import com.hugmount.helloboot.thrift.server.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: Li Huiming
 * @Date: 2020/5/14
 */
@Service
@Slf4j
public class HelloServiceImpl implements HelloService.Iface, ApplicationListener<ApplicationContextEvent> {

    private static ApplicationContext applicationContext;

    @Override
    public UserInfo getUser(int id) throws TException {
        TestServiceImpl bean = applicationContext.getBean(TestServiceImpl.class);
        List<Test> testList = bean.getTestList(null);
        log.info("测试thrift获取数据库信息: {}", JSON.toJSONString(testList));
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername("lhm");
        userInfo.setPassword("SUCCESS");
        return userInfo;
    }

    @Override
    public void onApplicationEvent(ApplicationContextEvent applicationContextEvent) {
        applicationContext = applicationContextEvent.getApplicationContext();
    }

}
