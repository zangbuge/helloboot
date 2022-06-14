package com.hugmount.helloboot.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * 性能测试
 *
 * @author lhm
 * @date 2022/6/14
 */
// Throughput 吞吐量, AverageTime 调用执行的平均时长
@BenchmarkMode(Mode.Throughput)
// 预热次数, 方法再前几次调用, jvm未预热性能浮动较大
@Warmup(iterations = 3)
// 运行轮数 10次, 每轮进行的时长5秒
@Measurement(iterations = 10, time = 5)
// 8个线程同时跑
@Threads(8)
// 输出时间单位 毫秒
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class TestStringBuffer {

    public static void main(String[] args) {
        Options opt = new OptionsBuilder()
                .include(TestStringBuffer.class.getSimpleName())
                .resultFormat(ResultFormatType.JSON).build();
        try {
            new Runner(opt).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Benchmark
    public String testString() {
        int times = 100;
        String str = "";
        for (int i = 0; i < times; i++) {
            str += i;
        }
        return str;
    }

    @Benchmark
    public String testBuilder() {
        int times = 100;
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < times; i++) {
            str.append(i);
        }
        return str.toString();
    }

}
