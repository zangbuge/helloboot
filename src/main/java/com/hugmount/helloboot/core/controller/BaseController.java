package com.hugmount.helloboot.core.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Li Huiming
 * @date 2021/8/21
 */
@RestController
public class BaseController {

    @GetMapping("/health")
    public String health() {
        return "success";
    }

}
