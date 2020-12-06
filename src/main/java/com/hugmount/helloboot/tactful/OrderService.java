package com.hugmount.helloboot.tactful;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: Li Huiming
 * @Date: 2020/12/5
 */
@Slf4j
@Service
public class OrderService {

    private Map<String, OrderHandler> orderHandlerMap;

    /**
     * Map的key使用 String[] 替代 HandlerType 控制更加灵活, 同时避免 HandlerType实现类重写hashcode方法
     * @param handlerList
     */
    @Autowired
    public void orderHandlerMap(List<OrderHandler> handlerList) {
        orderHandlerMap = handlerList.stream().collect(
                // 第一个参数作为map的key, 第二个参数作为map的value
                Collectors.toMap(handler -> {
                            HandlerType annotation = AnnotationUtils.findAnnotation(handler.getClass(), HandlerType.class);
                            String type = HandlerTypeImpl.getType(annotation);
                            return type;
                        },
                        v -> v, (v1, v2) -> v1) // 第三个参数 list中可能有重复值, 如果v1, v2有重复, 选取第一个作为值
        );
    }

    /**
     * 下单
     * 策略模式, 保证下单核心逻辑不会改变
     */
    public void placeOrder(Object order) {
        // TODO 数据正确性校验
        // TODO 数据业务校验
        Map<String, String> orderMap = (Map<String, String>) order;

        String channel = orderMap.get("channel");
        String platform = orderMap.get("platform");
        String payType = orderMap.get("payType");

        String[] factor = {"ALI", "ALI", "GX"};
        HandlerTypeImpl handlerType = new HandlerTypeImpl(factor);
        String type = handlerType.getType();
        // 通过HandlerType获取对应的handler具体处理
        OrderHandler orderHandler = orderHandlerMap.get(type);
        Object handleRes = orderHandler.handle(order);
        // TODO 对处理结果进行后续处理

        log.info("订单处理完成");
    }

}
