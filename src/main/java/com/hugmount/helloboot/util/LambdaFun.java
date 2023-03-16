package com.hugmount.helloboot.util;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * @author lhm
 * @date 2023/3/15
 */
public interface LambdaFun<T, R> extends Function<T, R>, Serializable {
    default SerializedLambda getSerializedLambda() {
        try {
            Method writeReplace = this.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);
            Object sl = writeReplace.invoke(this);
            return (SerializedLambda) sl;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
