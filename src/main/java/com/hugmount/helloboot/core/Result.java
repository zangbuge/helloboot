package com.hugmount.helloboot.core;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/** 服务器响应vo
 * @Author: Li Huiming
 * @Date: 2019/3/14
 */

@ApiModel(value = "Result" ,discriminator = "服务器响应vo")
@Data
public class Result<T> implements Serializable{

	private static final long serialVersionUID = 1L;

	//成功
	private final static String SUCCESS = "0";
	
	//失败
	private final static String ERROR = "1";

	//异常
	private  final static String EXCEPTION = "-1";

	@ApiModelProperty(value = "代码")
	private String code;

	@ApiModelProperty(value = "提示信息")
	private String msg;

	@ApiModelProperty(value = "数据")
	private T data;

	public boolean isSuccess(){
		return SUCCESS.equals(code);
	}
	
	public static <T> Result<T> createBySuccess(String msg){
		Result<T> result = new Result<>();
		result.setCode(SUCCESS);
		result.setMsg(msg);
		return result;
	}
	
	public static <T> Result<T> createBySuccess(String msg ,T data){
		Result<T> result = new Result<>();
		result.setCode(SUCCESS);
		result.setMsg(msg);
		result.setData(data);
		return result;
	}
	
	public static <T> Result<T> createByError(String msg){
		Result<T> result = new Result<>();
		result.setCode(ERROR);
		result.setMsg(msg);
		return result;
	}

	public static <T> Result<T> createByException(String msg){
		Result<T> result = new Result<>();
		result.setCode(EXCEPTION);
		result.setMsg(msg);
		return result;
	}

}
