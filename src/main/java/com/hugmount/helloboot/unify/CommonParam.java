package com.hugmount.helloboot.unify;

import lombok.Data;

/**
 * @author Li Huiming
 * @date 2021/12/22
 */
@Data
public class CommonParam<P> {

    private String appId;

    private String sign;

    private String route;

    private P param;

}
