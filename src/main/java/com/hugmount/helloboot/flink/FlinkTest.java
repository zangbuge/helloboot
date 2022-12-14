package com.hugmount.helloboot.flink;

import com.hugmount.helloboot.test.pojo.User;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author: lhm
 * @date: 2022/12/14
 */
public class FlinkTest {
    public static void main(String[] args) throws Exception {
        //获取执行环境
        StreamExecutionEnvironment fsEnv = StreamExecutionEnvironment.getExecutionEnvironment();
        //获取数据源
        DataStreamSource<User> source = fsEnv.addSource(new MysqlSource());
        //输出数据
        source.addSink(new MysqlSink());
        //执行该逻辑
        fsEnv.execute();
        System.out.println("执行完成");
    }
}
