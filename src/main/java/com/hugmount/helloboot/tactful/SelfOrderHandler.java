package com.hugmount.helloboot.tactful;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author: Li Huiming
 * @Date: 2020/12/5
 */
@Slf4j
@HandlerType(factor = {"SELF", "WX", "GX"})
@Service
public class SelfOrderHandler implements OrderHandler {
    @Override
    public Object handle(Object obj) {
        log.info("自营平台");
        return null;
    }
}
