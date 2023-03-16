package com.hugmount.helloboot.util;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.List;

/**
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

    public <T> void batch(SqlSessionFactory sqlSessionFactory, List<T> list, LambdaFun<T, Integer> action) {
        SqlSession sqlSession = null;
        try {
            String implClass = action.getSerializedLambda().getImplClass();
            String clazzPath = implClass.replace("/", ".");
            Class<?> aClass = Class.forName(clazzPath);
            String implMethodName = action.getSerializedLambda().getImplMethodName();
            sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
            Object mapper = sqlSession.getMapper(aClass);
            for (T item : list) {
                Method method = aClass.getDeclaredMethod(implMethodName, item.getClass());
                method.invoke(mapper, item);
            }
            sqlSession.commit();
        } catch (Exception e) {
            throw new RuntimeException("批量操作报错", e);
        } finally {
            sqlSession.close();
        }
    }

}
