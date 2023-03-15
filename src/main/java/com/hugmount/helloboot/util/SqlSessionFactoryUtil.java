package com.hugmount.helloboot.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Function;

/**
 * @author: lhm
 * @date: 2023/3/15
 */

@Slf4j
@Service
public class SqlSessionFactoryUtil {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    public <T> void batch(List<T> list, Class<?> mapperClazz, Function<T, Integer> action) {
        SqlSession sqlSession = null;
        try {
            sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
            Object mapper = sqlSession.getMapper(mapperClazz);
            Method[] declaredMethods = action.getClass().getDeclaredMethods();
            String name = declaredMethods[0].getName();
            Class<?>[] parameterTypes = declaredMethods[0].getParameterTypes();
            list.stream().forEach(item -> {
                try {
                    Method method = mapper.getClass().getMethod(name, parameterTypes);
                    method.invoke(mapper, item);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            sqlSession.commit();
        } catch (Exception e) {
            log.info("批量操作报错,参数类型有误");
            throw new RuntimeException(e);
        } finally {
            sqlSession.close();
        }
    }

}
