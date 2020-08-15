package com.hugmount.helloboot.thrift.config;

import com.hugmount.helloboot.thrift.server.HelloService;
import com.hugmount.helloboot.thrift.server.impl.HelloServiceImpl;
import com.hugmount.helloboot.thrift.server.impl.ThriftServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author: Li Huiming
 * @Date: 2020/5/14
 */
@Slf4j
public class ThriftServerStartListener implements ServletContextListener {

    private static ThriftServer thriftServer;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
            thriftServer = context.getBean(ThriftServer.class);
            thriftServer.start();

            // 代理模式
//            ThriftServerProxy thriftServerProxy = thriftServerProxy();
//            thriftServerProxy.start();

        }catch (Exception e){
            log.error("监听thrift服务启动异常", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }

    /**
     * 该方式注册服务有注解取代
     */
    /*@Bean
    public ThriftServerProxy thriftServerProxy() {
        ThriftServerProxy thriftServerProxy = new ThriftServerProxy();
        thriftServerProxy.setPort(9800);
        ThriftServerProxy.Processor processor = new ThriftServerProxy.Processor();
        processor.setServiceInterface(HelloService.class);
        processor.setServiceImplObject(new HelloServiceImpl());
        List<ThriftServerProxy.Processor> processorList = new LinkedList<>();
        processorList.add(processor);
        thriftServerProxy.setProcessors(processorList);
        return thriftServerProxy;
    }*/

}
