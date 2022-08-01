package com.hugmount.helloboot.config;

import com.hugmount.helloboot.core.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: lhm
 * @date: 2022/8/1
 */
@Slf4j
@ControllerAdvice
public class ExceptionConfig {

    @ResponseBody
    @ExceptionHandler
    public Result<Object> exceptionHandler(Exception e) {
        // 处理@Valid校验
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException notValidException = (MethodArgumentNotValidException) e;
            List<ObjectError> allErrors = notValidException.getBindingResult().getAllErrors();
            List<String> collect = allErrors.stream().map(item -> item.getDefaultMessage()).collect(Collectors.toList());
            return Result.createByError(collect.toString());
        }
        // 处理Assert.isTrue()断言
        if (e instanceof IllegalArgumentException) {
            return Result.createByError(e.getMessage());
        }
        log.error("系统异常", e);
        return Result.createByException("系统繁忙,请稍后重试");
    }

}
