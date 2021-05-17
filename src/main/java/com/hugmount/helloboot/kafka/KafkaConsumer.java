package com.hugmount.helloboot.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @Author: Li Huiming
 * @Date: 2021/1/25
 */
//@Component
@Slf4j
public class KafkaConsumer {

    @KafkaListener(topics = {"topic1"})
    public void onMessage(ConsumerRecord<String, String> record) {
        log.info("已消费: {}", record.value());
    }

}
