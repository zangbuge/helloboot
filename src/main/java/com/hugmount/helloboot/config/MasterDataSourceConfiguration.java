package com.hugmount.helloboot.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.github.pagehelper.PageInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @Author: Li Huiming
 * @Date: 2019/3/13
 */
@Configuration
@MapperScan(basePackages = "com.hugmount.helloboot.test.mapper", sqlSessionTemplateRef = "masterSqlSessionTemplate")
public class MasterDataSourceConfiguration {

    /**
     * mysql8出现XA该错误: com.mysql.cj.jdbc.MysqlXAException: XAER_RMERR:
     * Fatal error occurred in the transaction branch - check your data for consistency
     * 当前访问mysql的账号root缺少系统权限，执行以下sql语句即可:
     * GRANT XA_RECOVER_ADMIN ON *.* TO root@'%';
     *
     * @return
     */
    @Primary
    @Bean(name = "masterDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.master")
    public DataSource dataSource() {
        // hikari
//        return DataSourceBuilder.create().build();
        return new DruidDataSourceBuilder().build();
    }

    @Bean(name = "masterSqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier("masterDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
        bean.setDataSource(dataSource);
//        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:com/hugmount/helloboot/test/mapper/**/*Mapper.xml"));
        Interceptor pageInterceptor = getPageInterceptor();
        bean.setPlugins(new Interceptor[]{pageInterceptor});
        return bean.getObject();
    }

    private Interceptor getPageInterceptor() {
        //设置分页
        Interceptor interceptor = new PageInterceptor();
        Properties properties = new Properties();
        //数据库方言
        properties.setProperty("helperDialect", "mysql");
        //是否将参数offset作为PageNum使用
        properties.setProperty("offsetAsPageNum", "true");
        //是否进行count查询
        properties.setProperty("rowBoundsWithCount", "true");
        //是否分页合理化
        properties.setProperty("reasonable", "false");
        interceptor.setProperties(properties);
        return interceptor;
    }

    @Bean(name = "masterSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("masterSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    /**
     * 访问sql监控页面: http://localhost:8086/helloboot/druid/sql.html
     *
     * @return
     * @throws Exception
     */
    @Bean
    public ServletRegistrationBean druidServlet() {
        ServletRegistrationBean reg = new ServletRegistrationBean();
        reg.setServlet(new StatViewServlet());
        //配置访问URL
        reg.addUrlMappings("/druid/*");
        //配置用户名，这里使用数据库账号。
        reg.addInitParameter("loginUsername", "admin");
        //配置用户密码，这里使用数据库密码 也可密码加密 ConfigTools.decrypt(publicKey,password)
        reg.addInitParameter("loginPassword", "123456");
        //是否启用慢sql
        reg.addInitParameter("logSlowSql", "true");

        return reg;
    }


    /**
     * sql监控, 注册一个：filterRegistrationBean
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean druidStatFilter() {

        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());

        //添加过滤规则.
        filterRegistrationBean.addUrlPatterns("/*");

        //添加不需要忽略的格式信息.
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }

}
