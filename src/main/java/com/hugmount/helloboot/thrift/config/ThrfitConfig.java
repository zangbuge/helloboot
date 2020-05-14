package com.hugmount.helloboot.thrift.config;

import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Li Huiming
 * @Date: 2020/5/14
 */
@Configuration
public class ThrfitConfig {

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Bean
    public ServletListenerRegistrationBean listenerRegist() {
        ServletListenerRegistrationBean bean = new ServletListenerRegistrationBean();
        bean.setListener(new ThriftServerStartListener());
        return bean;
    }

}
