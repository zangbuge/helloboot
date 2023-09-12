package com.hugmount.helloboot.core;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 服务器响应vo
 * 阿里规范：命名必须遵守驼峰形式，DO/DTO/VO 除外。 正确命名如： XmlService, TcpService
 * 1.数据对象：xxxDO，xxx即为数据表名；
 * 2.数据传输对象：xxxDTO，xxx为业务领域相关的名称；
 * 3.展示对象：xxxVO，xxx一般为网页的名称；
 * 4.POJO 是DO/DTO/BO/VO的统称，禁止命名成xxxPOJO。
 *
 * @Author: Li Huiming
 * @Date: 2019/3/14
 */

@ApiModel(value = "Result", discriminator = "服务器响应vo")
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    //成功
    private final static String SUCCESS = "0";

    //失败
    private final static String ERROR = "1";

    //异常
    private final static String EXCEPTION = "-1";

    @ApiModelProperty(value = "代码")
    private String code;

    @ApiModelProperty(value = "提示信息")
    private String msg;

    @ApiModelProperty(value = "数据")
    private T data;

    public boolean succeed() {
        return SUCCESS.equals(code);
    }

    public static <T> Result<T> createBySuccess(String msg) {
        Result<T> result = new Result<>();
        result.setCode(SUCCESS);
        result.setMsg(msg);
        return result;
    }

    public static <T> Result<T> createBySuccess(String msg, T data) {
        Result<T> result = new Result<>();
        result.setCode(SUCCESS);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> createByError(String msg) {
        Result<T> result = new Result<>();
        result.setCode(ERROR);
        result.setMsg(msg);
        return result;
    }

    public static <T> Result<T> createByException(String msg) {
        Result<T> result = new Result<>();
        result.setCode(EXCEPTION);
        result.setMsg(msg);
        return result;
    }

}
