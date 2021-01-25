package com.hugmount.helloboot.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Li Huiming
 * @Date: 2021/1/25
 */
@Slf4j
@RestController
@RequestMapping("/kafka")
public class KafkaProducerController {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @RequestMapping("/sendMsg/{msg}")
    public String sendMsg(@PathVariable("msg") String msg) {
        kafkaTemplate.send("topic1", msg);
        log.info("发送消息: {}", msg);
        return "success";
    }

}
