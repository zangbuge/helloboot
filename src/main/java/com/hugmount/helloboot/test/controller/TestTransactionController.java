package com.hugmount.helloboot.test.controller;

import com.hugmount.helloboot.core.Result;
import com.hugmount.helloboot.test.mapper.TestMapper;
import com.hugmount.helloboot.test.pojo.Test;
import com.hugmount.helloboot.util.ThreadUtil;
import com.hugmount.helloboot.util.TransactionManagerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * @author: lhm
 * @date: 2023/8/9
 */
@RestController
public class TestTransactionController {

    @Autowired
    private TransactionManagerUtil transactionManagerUtil;

    @Autowired
    private TestMapper testMapper;

    @GetMapping("/testTransaction")
    public Result<String> testTransaction() {
        List<String> strings = new ArrayList<>();
        strings.add("1lhm");
        strings.add("2lhm");
        strings.add("3lhm");
        strings.add("error");
        List<TransactionManagerUtil.Task> tasks = new CopyOnWriteArrayList<>();
        strings.forEach(it -> {
            tasks.add(() -> {
                Test test = new Test();
                test.setUsername(it + System.currentTimeMillis());
                testMapper.insert(test);
                if (it.contains("error")) {
                    throw new RuntimeException("异常");
                }
            });
        });
        transactionManagerUtil.execute(tasks, ThreadUtil.getExecutorService(), 5, TimeUnit.SECONDS);
        return Result.createBySuccess("success");
    }

}
