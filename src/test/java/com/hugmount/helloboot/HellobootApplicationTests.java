package com.hugmount.helloboot;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("db_oracle")
public class HellobootApplicationTests {

	@Test
	public void contextLoads() {
		System.out.println("测试完成");
	}

}
