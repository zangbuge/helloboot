package com.hugmount.helloboot;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
//@ActiveProfiles("db_oracle") //激活@profile注解
public class HellobootApplicationTests {

	@Autowired
	ApplicationContext applicationContext;

	@Test
	public void contextLoads() {
		Map<String, com.hugmount.helloboot.test.pojo.Test> map = applicationContext.getBeansOfType(com.hugmount.helloboot.test.pojo.Test.class);
		System.out.println(map);
		System.out.println("测试完成");
	}

}
