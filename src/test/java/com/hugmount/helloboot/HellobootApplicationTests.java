package com.hugmount.helloboot;

import cn.hutool.json.JSONUtil;
import com.hugmount.helloboot.test.service.impl.TestServiceImpl;
import com.hugmount.helloboot.thrift.server.UserInfo;
import com.hugmount.helloboot.thrift.server.impl.HelloServiceImpl;
import org.apache.thrift.TException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
// 注意这里是静态引入
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private TestServiceImpl testService;

    /**
     * 目标测试类锚点
     */
    @Autowired
    @InjectMocks
    private HelloServiceImpl helloService;

    @Test
    public void contextLoads() {
        Map<String, com.hugmount.helloboot.test.pojo.Test> map = applicationContext.getBeansOfType(com.hugmount.helloboot.test.pojo.Test.class);
        System.out.println(map);
        System.out.println("测试完成");
        List<com.hugmount.helloboot.test.pojo.Test> list = new ArrayList<>();
        com.hugmount.helloboot.test.pojo.Test test = new com.hugmount.helloboot.test.pojo.Test();
        test.setPhone("158");
        list.add(test);
        when(testService.getTestList(any())).thenReturn(list);
        try {
            UserInfo user = helloService.getUser(1);
            System.out.println("mock数据" + JSONUtil.toJsonStr(user));
        } catch (TException e) {
            e.printStackTrace();
        }
    }

}
