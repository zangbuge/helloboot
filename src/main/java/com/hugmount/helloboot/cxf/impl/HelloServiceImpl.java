package com.hugmount.helloboot.cxf.impl;

import com.hugmount.helloboot.cxf.HelloService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Li Huiming
 * @date 2022/4/11
 */

@Slf4j
public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHello(String msg) {
        log.info("cxf服务收到信息: {}", msg);
        return "hello" + msg;
    }

}
