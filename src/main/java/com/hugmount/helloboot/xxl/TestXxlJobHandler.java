package com.hugmount.helloboot.xxl;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author: lhm
 * @date: 2023/1/3
 */

@Component
public class TestXxlJobHandler {

    @XxlJob("testJobHandler")
    public ReturnT<String> demoJobHandler(String param) {
        System.out.println(new Date() + "xxl-job触发参数: " + param);
        return ReturnT.SUCCESS;
    }

}
