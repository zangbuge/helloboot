package com.hugmount.helloboot;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.github.pagehelper.autoconfigure.PageHelperAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

/**
 * @Author: Li Huiming
 * @Date: 2019/3/13
 */

// @MapperScan("com.hugmount.helloboot.*.mapper") 和 @Mapper 二选一即可.
// 扫描包路径不要太大包含Service层,否则可能在service层报错Invalid bound statement (not found)
// exclude = 将排除不加载这些类, 避免默认加载这些未使用的类而导致报错的问题
@SpringBootApplication(exclude = {MongoAutoConfiguration.class
        , HibernateJpaAutoConfiguration.class
        , PageHelperAutoConfiguration.class //使用多数据源配置分页时排除
})
@EnableDubbo
@EnableApolloConfig
public class HellobootApplication {

    public static void main(String[] args) {
        SpringApplication.run(HellobootApplication.class, args);
    }

}
