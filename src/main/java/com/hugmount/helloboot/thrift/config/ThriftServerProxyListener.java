package com.hugmount.helloboot.thrift.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/** 监听注册thrift服务并启动
 *  条件注入: @ConditionalOnExpression("'${thrift.open}'.equals('true')")
 * @Author: Li Huiming
 * @Date: 2020/8/15
 */
@Slf4j
@Component
@ConditionalOnBean(ThriftServerProxy.class)
public class ThriftServerProxyListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(ThriftService.class);
        Iterator<Map.Entry<String, Object>> iterator = beans.entrySet().iterator();

        ThriftServerProxy thriftServerProxy = applicationContext.getBean(ThriftServerProxy.class);
        List<ThriftServerProxy.Processor> processorList = new LinkedList<>();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> next = iterator.next();
            Object value = next.getValue();
            String canonicalName = value.getClass().getCanonicalName();
            String ifaceName = null;
            Class<?>[] interfaces = value.getClass().getInterfaces();
            for (Class clazz : interfaces) {
                String str = clazz.getCanonicalName();
                if (str.contains("Iface")) {
                    ifaceName = str;
                    break;
                }
            }
            if (ifaceName == null) {
                String msg = "使用 @ThriftService 修饰的类必须实现thrift接口,错误类: " + canonicalName;
                throw new RuntimeException(msg);
            }
            Class<?> serviceClass;
            try {
                serviceClass = Class.forName(ifaceName.substring(0, ifaceName.indexOf(".Iface")));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Thrift服务注册异常", e);
            }

            ThriftServerProxy.Processor processor = new ThriftServerProxy.Processor();
            processor.setServiceInterface(serviceClass);
            processor.setServiceImplObject(value);
            processorList.add(processor);

        }
        thriftServerProxy.setProcessors(processorList);
        thriftServerProxy.start();
    }

}
