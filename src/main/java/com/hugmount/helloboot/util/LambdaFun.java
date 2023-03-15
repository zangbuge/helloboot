package com.hugmount.helloboot.util;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @author lhm
 * @date 2023/3/15
 */
public interface LambdaFun<T, R> extends Function<T, R>, Serializable {
}
