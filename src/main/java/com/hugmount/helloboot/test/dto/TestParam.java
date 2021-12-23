package com.hugmount.helloboot.test.dto;

import com.hugmount.helloboot.unify.IParam;
import lombok.Data;

/**
 * @author Li Huiming
 * @date 2021/12/21
 */
@Data
public class TestParam implements IParam<TestParam> {

    private String userName;

}
