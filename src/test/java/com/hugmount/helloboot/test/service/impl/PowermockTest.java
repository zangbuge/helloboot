package com.hugmount.helloboot.test.service.impl;

import com.hugmount.helloboot.util.StrUtil;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author lhm
 * @date 2024/7/10
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(StrUtil.class)
@PowerMockIgnore({"javax.management.*", "javax.script.*"}) // 忽略jdk兼容问题报错
public class PowermockTest {

    @InjectMocks
    private TestServiceImpl testService;

    /**
     * spy 静态方法
     */
    @SneakyThrows
    @Test
    public void test() {
        PowerMockito.spy(StrUtil.class);
        String url = "http://www.baidu.com/test?name=lhm&phone=123";
        String res = "hello word";
        PowerMockito.doReturn(res).when(StrUtil.class, "getUrlAddr", url);
        String str = testService.testStrUtil(url);
        System.out.println(str);
    }

}
