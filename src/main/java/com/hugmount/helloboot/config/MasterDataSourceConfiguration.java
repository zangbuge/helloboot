package com.hugmount.helloboot.config;

import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.github.pagehelper.PageInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @Author: Li Huiming
 * @Date: 2019/3/13
 */
@Configuration
@MapperScan(basePackages = "com.hugmount.helloboot.product.mapper", sqlSessionTemplateRef  = "masterSqlSessionTemplate")
public class MasterDataSourceConfiguration {

    @Value("${spring.datasource.master.driver-class-name}")
    private String driverClassName;

    @Value("${spring.datasource.master.url}")
    private String url;

    @Value("${spring.datasource.master.username}")
    private String username;

    @Value("${spring.datasource.master.password}")
    private String password;

    @Bean(name = "masterDataSource")
    @Primary
    public DataSource dataSource() {
        AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();
        DruidXADataSource dataSource = new DruidXADataSource ();
        dataSource.setDriverClassName(this.driverClassName);
        dataSource.setUrl(this.url);
        dataSource.setUsername(this.username);
        dataSource.setPassword(this.password);
        try {
            //没有该配置,druid中sql监控不会显示
            dataSource.setFilters("stat,wall,slf4j");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //最优配置
        dataSource.setInitialSize(3);
        dataSource.setMinIdle(3);
        dataSource.setMaxActive(15);
        //获取连接超时时间（单位：ms）
        dataSource.setMaxWait(5000);
        //连接有效性检测时间(单位:ms)
        dataSource.setTimeBetweenEvictionRunsMillis(9000);
        //获取连接检测,默认为关闭
        dataSource.setTestOnBorrow(false);
        //归还连接检测,默认为关闭
        dataSource.setTestOnReturn(false);
        //最大空闲时间(单位ms)默认为30分钟
        dataSource.setMinEvictableIdleTimeMillis(1800000);
        atomikosDataSourceBean.setXaDataSource(dataSource);

        return atomikosDataSourceBean;
    }

    @Bean(name = "masterSqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier("masterDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:com/hugmount/helloboot/product/mapper/**/*Mapper.xml"));
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
        bean.setPlugins(new Interceptor[] {interceptor});

        return bean.getObject();
    }

    @Bean(name = "masterSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("masterSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    /**
     * 访问sql监控页面: http://localhost:8086/helloboot/druid/sql.html
     * @return
     * @throws Exception
     */
    @Bean
    public ServletRegistrationBean druidServlet() throws Exception {
        ServletRegistrationBean reg = new ServletRegistrationBean();
        reg.setServlet(new StatViewServlet());
        reg.addUrlMappings("/druid/*");//配置访问URL
        reg.addInitParameter("loginUsername", "admin");  //配置用户名，这里使用数据库账号。
//        reg.addInitParameter("loginPassword", ConfigTools.decrypt(publicKey,password));  //配置用户密码，这里使用数据库密码
        reg.addInitParameter("loginPassword", "123");
        reg.addInitParameter("logSlowSql", "true");   //是否启用慢sql

        return reg;
    }


    /**
     * sql监控, 注册一个：filterRegistrationBean
     * @return
     */
    @Bean
    public FilterRegistrationBean druidStatFilter(){

        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());

        //添加过滤规则.
        filterRegistrationBean.addUrlPatterns("/*");

        //添加不需要忽略的格式信息.
        filterRegistrationBean.addInitParameter("exclusions","*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }

}
