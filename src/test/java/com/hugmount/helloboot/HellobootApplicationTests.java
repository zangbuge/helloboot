package com.hugmount.helloboot;

import cn.hutool.json.JSONUtil;
import com.hugmount.helloboot.test.mapper.TestMapper;
import com.hugmount.helloboot.test.service.impl.TestServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
// 注意Mockito这里是静态引入


@RunWith(SpringRunner.class)
@SpringBootTest
//@ActiveProfiles("db_oracle") //激活@profile注解
public class HellobootApplicationTests {

    @Autowired
    ApplicationContext applicationContext;

    /**
     * 被依赖标记
     */
    @Mock
    private TestMapper testMapper;

    /**
     * 目标测试类锚点
     */
    @Autowired
    @InjectMocks
    private TestServiceImpl testService;

    /**
     * junit测试类中任意测试方法执行前,都会执行此方法
     */
    @Before
    public void init() {
        // 初始化所需的资源
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void contextLoads() {
        Map<String, com.hugmount.helloboot.test.pojo.Test> map = applicationContext.getBeansOfType(com.hugmount.helloboot.test.pojo.Test.class);
        System.out.println(map);
        System.out.println("测试完成");
        List<com.hugmount.helloboot.test.pojo.Test> list = new ArrayList<>();
        com.hugmount.helloboot.test.pojo.Test test = new com.hugmount.helloboot.test.pojo.Test();
        test.setPhone("1589799");
        list.add(test);
        when(testMapper.getTestList(any())).thenReturn(list);
        List<com.hugmount.helloboot.test.pojo.Test> testList = testService.getTestList(test);
        System.out.println("mock数据" + JSONUtil.toJsonStr(testList));
    }

}
