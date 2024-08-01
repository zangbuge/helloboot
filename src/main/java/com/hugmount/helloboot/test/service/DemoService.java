package com.hugmount.helloboot.test.service;

import org.springframework.stereotype.Service;

/**
 * @author lhm
 * @date 2024/7/30
 */
@Service
public class DemoService {

    public String hi(String name) {
        return "hi " + name;
    }

    public String read(String name) {
        return "read " + name;
    }

}
