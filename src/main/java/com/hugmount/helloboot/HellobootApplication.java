package com.hugmount.helloboot;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import com.github.pagehelper.autoconfigure.PageHelperAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
// exclude = 将排除不加载这些类, 避免默认加载这些未使用的类而导致报错的问题
@SpringBootApplication(exclude = {MongoAutoConfiguration.class
		, HibernateJpaAutoConfiguration.class
		, PageHelperAutoConfiguration.class //使用多数据源配置分页时排除
})
@MapperScan("com.hugmount.helloboot.*.mapper")
@EnableDubbo
public class HellobootApplication {

	public static void main(String[] args) {
		SpringApplication.run(HellobootApplication.class, args);
	}

}
