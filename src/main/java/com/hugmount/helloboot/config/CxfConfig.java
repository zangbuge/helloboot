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
        return new ServletRegistrationBean(new CXFServlet(), "/ws/*");
    }

    @Bean(name = Bus.DEFAULT_BUS_ID)
    public SpringBus springBus() {
        return new SpringBus();
    }

    /**
     * HelloServiceImpl 必须在这里注入, 不能使用@Service
     *
     * @return
     */
    @Bean
    public HelloService userService() {
        return new HelloServiceImpl();
    }

    /**
     * 必须绑定webservice接口
     * 若多个服务配置依次注入
     *
     * @return
     */
    @Bean
    public Endpoint helloServiceEndpoint() {
        // 绑定webservice接口
        EndpointImpl endpoint = new EndpointImpl(springBus(), userService());
        // 发布的名称
        endpoint.publish("/helloService");
        return endpoint;
    }

}
