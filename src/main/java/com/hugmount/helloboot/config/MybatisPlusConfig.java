package com.hugmount.helloboot.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author lhm
 * @date 2023/11/8
 */
@Slf4j
@Configuration
public class MybatisPlusConfig {

    @Value("${dynamicTableNameList:}")
    private String dynamicTableNameList;

    @Autowired
    private List<SqlSessionFactory> sqlSessionFactoryList;

    @PostConstruct
    public void addMyInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = mybatisPlusInterceptor();
        for (SqlSessionFactory sqlSessionFactory : sqlSessionFactoryList) {
            sqlSessionFactory.getConfiguration().addInterceptor(mybatisPlusInterceptor);
        }
    }

    /**
     * 动态传递表名
     *
     * @return
     */
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        log.info("动态传递表名配置: {}", dynamicTableNameList);
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
        dynamicTableNameInnerInterceptor.setTableNameHandler((sql, tableName) -> {
            if (checkDynamicTable(tableName)) {
                // 返回新的表名
                return dealTableName(tableName);
            }
            return tableName;
        });
        interceptor.addInnerInterceptor(dynamicTableNameInnerInterceptor);
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }

    boolean checkDynamicTable(String tableName) {
        if (dynamicTableNameList.contains(tableName)) {
            return true;
        }
        return false;
    }

    String dealTableName(String oldTable) {
        String tableName = TableNameHelper.getTableName();
        if (ObjectUtils.isEmpty(tableName)) {
            return oldTable;
        }
        // 全表名
        if (tableName.contains(oldTable)) {
            return tableName;
        }
        // tableName为后缀
        return oldTable + "_" + tableName;
    }

}
