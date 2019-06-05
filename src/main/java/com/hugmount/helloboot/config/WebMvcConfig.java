package com.hugmount.helloboot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @Author: Li Huiming
 * @Date: 2019/6/3
 */

@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    /**
     * 配置静态资源访问: http://localhost:8086/helloboot/store/123.png
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/store/**").addResourceLocations("classpath:/store/");
        super.addResourceHandlers(registry);
    }

}
