package com.hugmount.helloboot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

/** 直接继承WebMvcConfigurationSupport会使springboot默认配置失效
 * @Author: Li Huiming
 * @Date: 2019/6/3
 */

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 配置静态资源访问: http://localhost:8086/helloboot/store/123.png
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/store/**")
                .addResourceLocations("classpath:/store/");
    }

    /**
     * 添加拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SecurityInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/static/**");
    }


    /**
     *  添加主页
     * @param registry
     */
//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/").setViewName("index");
//    }


    /**
     * 设置时区
     */
    @PostConstruct
    public void timeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    }

}
