package com.hugmount.helloboot;

import cn.hutool.core.lang.func.Consumer3;
import cn.hutool.core.lang.func.Supplier2;
import com.hugmount.helloboot.test.pojo.User;
import com.hugmount.helloboot.util.POIUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 方法引用
 *
 * @author: lhm
 * @date: 2022/10/23
 */
public class TestSupplier {
    public static void main(String[] args) {
        // 单个参数有返回值
        Function<String, String> function = DigestUtils::md5Hex;
        String lhm = function.apply("lhm");
        System.out.println(lhm);

        // 无返回值
        Consumer3<SXSSFWorkbook, HttpServletResponse, String> downloadExcel = POIUtil::downloadExcel;

        // 多个入参有返回值
        Supplier2<SXSSFWorkbook, LinkedHashMap<String, String>, List<Map<String, Object>>> exportExcel = POIUtil::exportExcel;

        // 调用A方法, 将A方法内的变量传到外部使用
        TestSupplier testSupplier = new TestSupplier();
        String str = testSupplier.testFunction(arg -> arg + "lhm");
        System.out.println(str);

        // A方法处理后的结果, 提供表达式给外部使用
        testSupplier.testConsumer(msg -> {
            String s = msg + "lhm";
            System.out.println(s);
        });

        User user = null;
        String s = Optional.ofNullable(user).map(User::getUsername).orElse("1");
        System.out.println("获取值 " + s);
        // 为空直接阻断
        Optional.ofNullable(user).map(User::getUsername).orElseThrow(RuntimeException::new);


    }


    public String testFunction(Function<String, String> function) {
        return function.apply("hello");
    }

    public void testConsumer(Consumer<String> consumer) {
        String msg = "msg";
        consumer.accept(msg);
    }
}
