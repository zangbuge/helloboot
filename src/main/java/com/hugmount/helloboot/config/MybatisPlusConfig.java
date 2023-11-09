package com.hugmount.helloboot.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lhm
 * @date 2023/11/8
 */
@Slf4j
@Configuration
//@AutoConfigureAfter(PageHelperAutoConfiguration.class)
public class MybatisPlusConfig {

    @Value("${dynamicTableNameList:}")
    private String dynamicTableNameList;


    /**
     * 动态传递表名
     *
     * @return
     */
    @Bean
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
        // 全表名
        if (tableName.contains(oldTable)) {
            return tableName;
        }
        // 后缀
        return oldTable + "_" + TableNameHelper.getTableName();
    }

}
