package com.hugmount.helloboot.flink;

import com.hugmount.helloboot.test.pojo.User;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/**
 * @author: lhm
 * @date: 2022/12/13
 */
public class MysqlSink extends RichSinkFunction<User> {
    private Connection connection;
    private PreparedStatement preparedStatement;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        //加载数据库驱动
        Class.forName("com.mysql.cj.jdbc.Driver");
        //获取连接
        connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3308/catering", "root", "123456");
        //构建执行SQL
        preparedStatement = connection.prepareStatement("UPDATE t_test SET username = ?,password = ?");
    }

    @Override
    public void close() throws Exception {
        super.close();
        if (preparedStatement != null) {
            preparedStatement.close();
        }
        if (connection != null) {
            connection.close();
        }
        super.close();
    }

    @Override
    public void invoke(User value, Context context) throws Exception {
        try {
            //添加新数据，执行SQL
            preparedStatement.setString(1, "new code 1");
            preparedStatement.setString(2, "new state 2");
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
