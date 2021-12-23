package com.hugmount.helloboot.unify;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hugmount.helloboot.util.ClassUtil;
import com.hugmount.helloboot.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Li Huiming
 * @date 2021/12/21
 */
@Slf4j
@RestController
@RequestMapping("/unify")
public class UnifyController {

    /**
     * @param param: {"route":"TEST", "param":{"userName":"lhm"}}
     * @return
     */
    @PostMapping("/invoke")
    public CommonResult invoke(@RequestBody CommonParam<Object> param) {
        log.info("123");
        // todo 校验签名
        RouteEnum routeEnum = RouteEnum.valueOf(param.getRoute());
        AbstractHandler handler = (AbstractHandler) SpringUtil.getBean(routeEnum.getHandlerClass());
        Class actualType = ClassUtil.getSupperClassActualType(handler.getClass(), 1);
        String json = JSON.toJSONString(param.getParam());
        Object obj = JSONObject.parseObject(json, actualType);
        IParam iParam = (IParam) obj;
        CommonResult process = handler.process(iParam);
        return process;
    }

}
