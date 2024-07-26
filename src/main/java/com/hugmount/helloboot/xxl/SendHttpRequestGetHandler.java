package com.hugmount.helloboot.xxl;

import com.hugmount.helloboot.util.HttpClientUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * 定时任务触发url
 *
 * @author lhm
 * @date 2024/7/26
 */

@Slf4j
@Profile("test")
@Component
public class SendHttpRequestGetHandler {

    /**
     * 任务参数为将触发的url
     * 如: http://localhost:8086/helloboot/getCurDate
     *
     * @return
     */
    @XxlJob("sendHttpGetHandler")
    public ReturnT<String> sendHttpGet() {
        String jobParam = XxlJobHelper.getJobParam();
        log.info("SendHttpRequestGetHandler入参: {}", jobParam);
        String res = HttpClientUtil.doGet(jobParam, null);
        log.info("SendHttpRequestGetHandler响应: {}", res);
        return ReturnT.SUCCESS;
    }

}
