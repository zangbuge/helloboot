package com.hugmount.helloboot.test.service.impl;

import com.hugmount.helloboot.test.service.DemoService;
import com.hugmount.helloboot.util.StrUtil;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;

/**
 * PowerMockito使用junit4
 *
 * @author lhm
 * @date 2024/7/10
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({TestServiceImpl.class, DemoService.class, StrUtil.class})
@PowerMockIgnore({"javax.management.*", "javax.script.*", "javax.net.ssl.*", "javax.crypto.*"})
// 忽略jdk兼容问题报错, http证书问题, sm4加密问题
public class PowermockTest {

    /**
     * @Spy 必须在声明时创建实例
     */
    @Spy
    private DemoService demoService = new DemoService();

    @InjectMocks
    private TestServiceImpl testService;

    @Before
    public void init() {
        // 开启mock注解, 否则 @Spy, @Mock 等注解无效
        MockitoAnnotations.initMocks(this);
    }

    /**
     * 静态方法 spy
     */
    @SneakyThrows
    @Test
    public void testStatic() {
        PowerMockito.spy(StrUtil.class);
        String url = "http://www.baidu.com/test?name=lhm&phone=123";
        String res = "hello word";
        PowerMockito.doReturn(res).when(StrUtil.class, "getUrlAddr", url);
        String str = testService.testStrUtil(url);
        System.out.println(str);
    }

    /**
     * mock 特定参数
     */
    @SneakyThrows
    @Test
    public void testMockArg() {
        TestServiceImpl spy = PowerMockito.spy(testService);
        HashMap<String, Object> objectObjectHashMap = new HashMap<>();
        // 在mock方法时, 条件要么都是具体值; 要么都任意匹配; 如果某个参数要确定值, 其他任意值, 这个参数要使用eq(), Mockito.argThat() 特定值匹配其他也要用eq();
        PowerMockito.when(spy.testArgumentMatcher(Mockito.eq("hello"), Mockito.argThat(map -> "lhm".equals(map.get("name"))))).thenReturn("mock map success");
        String s = spy.testArgumentMatcher("1", objectObjectHashMap);
        Assert.assertEquals("1", s);
        String hello = spy.testArgumentMatcher("hello", objectObjectHashMap);
        Assert.assertEquals("hello", hello);
        objectObjectHashMap.put("name", "lhm");
        objectObjectHashMap.put("age", "18");
        String res = spy.testArgumentMatcher("hello", objectObjectHashMap);
        Assert.assertEquals("mock map success", res);
    }

    @SneakyThrows
    @Test
    public void testPrV() {
        // @PrepareForTest中添加目标测试类
        TestServiceImpl spy = PowerMockito.spy(testService);
        PowerMockito.doReturn("mock result").when(spy, "testPrV", "zh");

        // 调用私有方法 方式一
        Object obj = Whitebox.invokeMethod(spy, "testPrV", "lhm");
        System.out.println(obj);

        // 调用私有方法 方式二
        Object res = ReflectionTestUtils.invokeMethod(spy, "testPrV", "zh");
        System.out.println("符合mock条件: " + res);
    }

    @SneakyThrows
    @Test
    public void testSpy() {
        PowerMockito.doReturn("lhm(mock hi)").when(demoService).hi("lhm");
        String lhm = testService.testDemoSpy("lhm");
        System.out.println(lhm);
        String test = testService.testDemoSpy("test");
        System.out.println(test);
    }

}
