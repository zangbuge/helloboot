package com.hugmount.helloboot.cxf.impl;

import com.hugmount.helloboot.cxf.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * @author Li Huiming
 * @date 2022/4/11
 */

@Slf4j
//@WebService(serviceName = "cxfHelloService", // 对外发布的服务名称
//        targetNamespace = "http://cxf.ws.cignacmb.com/")
//@Service
public class HelloServiceImpl implements HelloService {

    //    @WebMethod
    @Override
    public String sayHello(String msg) {
        log.info("cxf服务收到信息: {}", msg);
        return "hello" + msg;
    }

}
