package com.hugmount.helloboot.config;

import com.hugmount.helloboot.cxf.HelloService;
import com.hugmount.helloboot.cxf.impl.HelloServiceImpl;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.ws.Endpoint;

/**
 * @author Li Huiming
 * @date 2022/4/11
 */
@Configuration
public class CxfConfig {

    @Bean
    public ServletRegistrationBean cxfServlet() {
        return new ServletRegistrationBean(new CXFServlet(), "/cxf/*");
    }

    @Bean(name = Bus.DEFAULT_BUS_ID)
    public SpringBus springBus() {
        return new SpringBus();
    }

    @Bean
    public HelloService userService() {
        return new HelloServiceImpl();
    }

    @Bean
    public Endpoint endpoint() {
        // 绑定webservice接口
        EndpointImpl endpoint = new EndpointImpl(springBus(), userService());
        // 发布的名称
        endpoint.publish("/cxfHelloService");
        return endpoint;
    }

}
