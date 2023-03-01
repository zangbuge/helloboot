package com.hugmount.helloboot.xxl;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author: lhm
 * @date: 2023/1/3
 */
@Profile("test")
@Component
public class TestXxlJobHandler {

    @XxlJob("testJobHandler")
    public ReturnT<String> demoJobHandler() {
        String jobParam = XxlJobHelper.getJobParam();
        System.out.println(new Date() + "xxl-job触发参数: " + jobParam);
        return ReturnT.SUCCESS;
    }

}
