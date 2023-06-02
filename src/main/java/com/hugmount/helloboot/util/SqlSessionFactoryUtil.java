package com.hugmount.helloboot.util;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.List;

/**
 * ExecutorType.BATCH 优于 foreach组装sql
 * foreach 最佳性能一个批次在10-100条之间, 且表字段>20性能极差
 *
 * @author: lhm
 * @date: 2023/3/15
 */

@Service
public class SqlSessionFactoryUtil {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;


    public <T> void batch(List<T> list, LambdaFun<T, Integer> action) {
        batch(sqlSessionFactory, list, action);
    }

    /**
     * 批量耗时仅为单条插入耗时的1/3
     *
     * @param sqlSessionFactory
     * @param list
     * @param action
     * @param <T>
     */
    public <T> void batch(SqlSessionFactory sqlSessionFactory, List<T> list, LambdaFun<T, Integer> action) {
        SqlSession sqlSession = null;
        try {
            String implClass = action.getSerializedLambda().getImplClass();
            String clazzPath = implClass.replace("/", ".");
            Class<?> aClass = Class.forName(clazzPath);
            String implMethodName = action.getSerializedLambda().getImplMethodName();
            // false 是否自动提交事务, 必须同时开启事务@Transactional,才会交给spring管理, 否则效率不会提升
            sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
            Object mapper = sqlSession.getMapper(aClass);
            for (T item : list) {
                Method method = aClass.getDeclaredMethod(implMethodName, item.getClass());
                method.invoke(mapper, item);
            }
            // 非事务环境下强制commit, 事务情况下该commit相当于无效
            sqlSession.commit();
            sqlSession.clearCache();
        } catch (Exception e) {
            sqlSession.rollback();
            throw new RuntimeException("批量操作报错", e);
        } finally {
            sqlSession.close();
        }
    }

}
