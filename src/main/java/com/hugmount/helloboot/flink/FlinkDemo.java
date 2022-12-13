package com.hugmount.helloboot.flink;

import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

/**
 * @author: lhm
 * @date: 2022/12/13
 */
public class FlinkDemo {
    public static void main(String[] args) {
        // 1.准备环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 设置运行模式批处理
        env.setRuntimeMode(RuntimeExecutionMode.AUTOMATIC);
        // 2.加载数据源
        DataStreamSource<String> elementsSource = env.fromElements("java,scala,php,c++",
                "java,scala,php", "java,scala", "java");
        // 3.数据转换
        DataStream<String> flatMap = elementsSource.flatMap(new FlatMapFunction<String, String>() {
            private static final long serialVersionUID = 6574499175567290355L;

            @Override
            public void flatMap(String element, Collector<String> out) {
                String[] wordArr = element.split(",");
                for (String word : wordArr) {
                    out.collect(word);
                }
            }
        });

        //DataStream 下边为DataStream子类
        SingleOutputStreamOperator<String> source = flatMap.map(new MapFunction<String, String>() {
            private static final long serialVersionUID = 6574499175567290355L;

            @Override
            public String map(String value) {
                return value.toUpperCase();
            }
        });

        // 4.数据输出
        source.print();

        // 5.执行程序
        try {
            String jobName = "flink-hello-world";
            env.execute(jobName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
