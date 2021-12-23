package com.hugmount.helloboot.unify.handler;

import com.alibaba.fastjson.JSON;
import com.hugmount.helloboot.test.dto.OrderInfo;
import com.hugmount.helloboot.test.dto.TestParam;
import com.hugmount.helloboot.unify.AbstractHandler;
import com.hugmount.helloboot.unify.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Li Huiming
 * @date 2021/12/21
 */
@Slf4j
@Service
public class TestHandler extends AbstractHandler<OrderInfo, TestParam> {

    @Override
    protected CommonResult<OrderInfo> process(TestParam param) {
        log.info("参数: {}", JSON.toJSONString(param));
        CommonResult<OrderInfo> result = new CommonResult<>();
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setAddr("北京");
        result.setData(orderInfo);
        result.setMsg("success");
        return result;
    }

}
