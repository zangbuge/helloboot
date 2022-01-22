package com.hugmount.helloboot.redis;

import com.hugmount.helloboot.util.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Profile("prod")
@Slf4j
@Component
public class TakeTask {

    @Resource(name = "rBlockingQueue")
    private RBlockingQueue<String> rBlockingQueue;

    @PostConstruct
    public void initTake() {
        ThreadUtil.execute(() -> {
            while (true) {
                try {
                    String msg = rBlockingQueue.take();
                    log.info("已消费: " + msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
