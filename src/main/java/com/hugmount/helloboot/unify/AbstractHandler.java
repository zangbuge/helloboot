package com.hugmount.helloboot.unify;


/**
 * 适配器类 HandlerAdapter
 * 使用Handler模式实现解耦, 可以避免请求的发送者与接收者之间的耦合
 * 增强代码的灵活性和可扩展性; 它可以方便地添加、修改或删除处理器，而无需修改其他发送者或接收者的代码
 * 使用场景(包括但不限于)
 * 1. 在一个系统中有多个对象可以处理同一请求，但具体由哪个对象处理该请求在运行时确定
 * 2. 需要动态地指定处理请求的对象，而不是在编译时指定
 * 3. 将一个系统中的多个对象组合成一个处理链，依次处理请求
 *
 * @author Li Huiming
 * @date 2021/12/21
 */
public abstract class AbstractHandler<R, P extends IParam<P>> {

    protected abstract CommonResult<R> process(P param);

}
