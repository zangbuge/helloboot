package com.hugmount.helloboot.test.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: lhm
 * @date: 2023/4/23
 */

@RestController
public class VersionController {

    @Value("${buildNumber}")
    private String buildNumber;

    @RequestMapping("/version")
    public String getVersion() {
        return buildNumber;
    }

}
