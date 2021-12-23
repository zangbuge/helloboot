package com.hugmount.helloboot.unify;

import com.hugmount.helloboot.unify.handler.TestHandler;
import lombok.Getter;

/**
 * @author Li Huiming
 * @date 2021/12/21
 */
public enum RouteEnum {

    TEST(TestHandler.class, "测试处理类");

    @Getter
    private Class handlerClass;

    @Getter
    private String handlerName;

    RouteEnum(Class handlerClass, String handlerName) {
        this.handlerClass = handlerClass;
        this.handlerName = handlerName;
    }

}