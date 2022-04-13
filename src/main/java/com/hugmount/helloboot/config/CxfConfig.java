package com.hugmount.helloboot.config;

import com.hugmount.helloboot.cxf.HelloService;
import com.hugmount.helloboot.cxf.impl.HelloServiceImpl;
import lombok.Setter;
import org.apache.cxf.Bus;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.ws.Endpoint;
import java.util.List;

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
        // 添加认证拦截
        endpoint.getInInterceptors().add(cxfAuthInterceptor());
        return endpoint;
    }

    @Bean
    public CxfAuthInterceptor cxfAuthInterceptor() {
        CxfAuthInterceptor authInterceptor = new CxfAuthInterceptor();
        authInterceptor.setUsername("lhm");
        authInterceptor.setPassword("123456");
        return authInterceptor;
    }


    /**
     * 添加安全认证拦截
     */
    public static class CxfAuthInterceptor extends AbstractPhaseInterceptor<SoapMessage> {
        @Setter
        private String username;
        @Setter
        private String password;

        /**
         * 在调用之前拦截
         */
        public CxfAuthInterceptor() {
            super(Phase.PRE_INVOKE);
        }

        @Override
        public void handleMessage(SoapMessage soap) throws Fault {
            List<Header> headers = soap.getHeaders();
            if (CollectionUtils.isEmpty(headers)) {
                throw new RuntimeException("WS无安全认证信息");
            }
            Header header = headers.get(0);
            Element el = (Element) header.getObject();
            NodeList username = el.getElementsByTagName("username");
            NodeList password = el.getElementsByTagName("password");
            if (username.getLength() < 1 || password.getLength() < 1) {
                throw new RuntimeException("WS安全认证失败");
            }
            if (!(this.username.equals(username.item(0).getTextContent())
                    && this.password.equals(password.item(0).getTextContent()))) {
                throw new RuntimeException("WS安全认证信息失败");
            }

        }
    }
}
