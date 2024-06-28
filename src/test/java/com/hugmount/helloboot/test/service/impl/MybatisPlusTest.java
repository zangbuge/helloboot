package com.hugmount.helloboot.test.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hugmount.helloboot.AbstractMockTest;
import com.hugmount.helloboot.test.mapper.TTestMapper;
import com.hugmount.helloboot.test.pojo.TTest;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

/**
 * @author: lhm
 * @date: 2023/9/23
 */
@RunWith(MockitoJUnitRunner.class)
public class MybatisPlusTest extends AbstractMockTest {

    @InjectMocks
    TestServiceImpl testService;

    @Mock
    private TTestMapper tTestMapper;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        // 初始化mybatisPlus配置，才能使用LambdaQueryWrapper
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), TTest.class);
    }

    /**
     * 若要mock mybatis-plus的ServiceImpl自带的 list(queryWrapper)方法
     * list实际调用的是mapper的selectList方式，所以mock时用
     * Mockito.when(mapper.selectList(any())).thenReturn(list)
     */
    @Test
    public void tes1() {
        TTest t = new TTest();
        t.setUsername("lhm");
        Mockito.when(tTestMapper.selectList(mpWrap(Wrappers.<TTest>lambdaQuery().eq(TTest::getId, 1L)))).thenReturn(Arrays.asList(t));
        // 粗略mock可不用mpWrap
        // Mockito.when(tTestMapper.selectList(Mockito.any())).thenReturn(Arrays.asList(t));
        List<TTest> testList = testService.getTTestList(t);
        System.out.println("非mock条件逻辑" + JSONUtil.toJsonStr(testList));
        t.setId(1L);
        List<TTest> list = testService.getTTestList(t);
        System.out.println("mock条件结果" + JSONUtil.toJsonStr(list));
    }

}
