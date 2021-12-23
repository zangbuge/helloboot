package com.hugmount.helloboot.unify;


/**
 * @author Li Huiming
 * @date 2021/12/21
 */
public abstract class AbstractHandler<R, P extends IParam<P>> {

    protected abstract CommonResult<R> process(P param);

}
