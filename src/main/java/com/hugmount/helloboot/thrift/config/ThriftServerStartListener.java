package com.hugmount.helloboot.thrift.config;

import com.hugmount.helloboot.thrift.server.impl.ThriftServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

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
        }catch (Exception e){
            log.error("监听thrift服务启动异常", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }

}
