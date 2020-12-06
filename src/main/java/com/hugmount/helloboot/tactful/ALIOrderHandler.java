package com.hugmount.helloboot.tactful;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author: Li Huiming
 * @Date: 2020/12/5
 */
@Slf4j
@HandlerType(factor = {"ALI", "ALI", "GX"})
@Service
public class ALIOrderHandler implements OrderHandler{

    @Override
    public Object handle(Object obj) {
        log.info("处理阿里订单");
        return null;
    }

}
