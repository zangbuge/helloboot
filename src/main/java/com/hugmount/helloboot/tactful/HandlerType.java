package com.hugmount.helloboot.tactful;

import java.lang.annotation.*;

/**
 * @Author: Li Huiming
 * @Date: 2020/12/5
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface HandlerType {

    // 控制类型的因子
    String[] factor();

}
