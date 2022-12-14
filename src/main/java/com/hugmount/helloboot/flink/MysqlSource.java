package com.hugmount.helloboot.flink;

import com.hugmount.helloboot.test.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author: lhm
 * @date: 2022/12/13
 */
@Slf4j
public class MysqlSource extends RichSourceFunction<User> {
    private Connection connection = null;
    private PreparedStatement ps = null;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        //加载数据库驱动
        Class.forName("com.mysql.cj.jdbc.Driver");
        //获取连接
        connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3308/catering", "root", "123456");
        //构建读取SQL
        ps = connection.prepareStatement("select *  from t_test");
    }

    @Override
    public void run(SourceContext<User> sourceContext) throws Exception {
        try {
            //执行读取操作
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                User vo = new User();
                vo.setUsername(resultSet.getString("username"));
                vo.setPassword(resultSet.getString("password"));
                sourceContext.collect(vo);
            }
        } catch (Exception e) {
            log.error("runException", e);
        }
    }

    @Override
    public void cancel() {
        try {
            super.close();
            if (connection != null) {
                connection.close();
            }
            if (ps != null) {
                ps.close();
            }
        } catch (Exception e) {
            log.error("runException", e);
        }
    }
}
