package com.hugmount.helloboot.unify;

import lombok.Data;

/**
 * @author Li Huiming
 * @date 2021/12/21
 */
@Data
public class CommonResult<R> {

    private String code;

    private String msg;

    private R data;

}
