package com.hugmount.helloboot.tactful;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Author: Li Huiming
 * @Date: 2020/12/5
 */

@Api("订单")
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @RequestMapping("/placeOrder")
    public Object placeOrder(@RequestParam Map<String, String> order) {
        orderService.placeOrder(order);
        return "success";
    }

}
