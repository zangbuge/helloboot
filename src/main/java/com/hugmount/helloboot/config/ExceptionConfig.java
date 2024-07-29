package com.hugmount.helloboot.config;

import com.hugmount.helloboot.core.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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
            String msg = collect.toString();
            log.info("处理Valid校验: {}", msg);
            return Result.createByError(msg);
        }

        /**
         *  处理断言{@link Assert#isTrue(boolean, java.lang.String)}
         */
        if (e instanceof IllegalArgumentException) {
            log.info("处理断言: {}", e.getMessage());
            return Result.createByError(e.getMessage());
        }
        // 参数类型错误
        if (e instanceof MethodArgumentTypeMismatchException) {
            log.info("接口入参类型错误: {}", e.getMessage());
            return Result.createByError("请求参数类型错误,请检查入参");
        }
        log.error("系统异常", e);
        return Result.createByException("系统繁忙,请稍后重试");
    }

}
