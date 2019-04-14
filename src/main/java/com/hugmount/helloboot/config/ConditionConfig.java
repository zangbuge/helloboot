package com.hugmount.helloboot.config;

import com.hugmount.helloboot.test.pojo.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConditionConfig {

    @Conditional({WindowsConditinal.class})
    @Bean("test1")
    public Test test1() {
        Test test = new Test();
        test.setId(111L);
        System.out.println("实例化windows");
        return test;
    }

    @Conditional({LinuxCondition.class})
    @Bean("test2")
    public Test test2() {
        Test test = new Test();
        test.setId(222L);
        System.out.println("实例化linux");
        return test;
    }

}
