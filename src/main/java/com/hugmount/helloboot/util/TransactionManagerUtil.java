package com.hugmount.helloboot.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 多线程事务保证一致工具类
 *
 * @author: lhm
 * @date: 2023/8/9
 */
@Slf4j
@Component
public class TransactionManagerUtil {

    public interface Task {
        void handle();
    }

    @Autowired
    private PlatformTransactionManager transactionManager;

    public boolean execute(List<Task> tasks, ExecutorService executor, int timeout, TimeUnit unit) {
        // 通过await和countDown方法实现多个线程之间的来回切换
        // 先开启自己的事务,并保存事务状态用于后面执行提交或者回滚操作
        // 数据处理完后,若正常结束将线程安全submit设置为true，否则为false
        // 等待所有子线程执行完后,根据标记的submit, 各个等待的子线程统一处理事务提交或回滚
        // 注意: 每个子线程都会创建一个数据库连接并阻塞,太大的线程数会导致出现死锁
        CountDownLatch mainTask = new CountDownLatch(1);
        CountDownLatch subTask = new CountDownLatch(tasks.size());
        AtomicBoolean submit = new AtomicBoolean(true);

        tasks.forEach(item -> {
            executor.execute(() -> {
                // 任何一个线程失败,直接结束当前线程
                if (!submit.get()) {
                    subTask.countDown();
                    return;
                }
                // 开启事务
                DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
                defaultTransactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                TransactionStatus transactionStatus = transactionManager.getTransaction(defaultTransactionDefinition);
                try {
                    // 执行具体业务
                    item.handle();
                } catch (Exception e) {
                    log.error("执行具体业务异常", e);
                    submit.set(false);
                }
                // 一阶段
                subTask.countDown();
                try {
                    // 三阶段 等主线程标记所有子线程执行完成, 提交事务
                    mainTask.await(timeout, unit);
                    // 是否提交事务
                    if (submit.get()) {
                        transactionManager.commit(transactionStatus);
                        return;
                    }
                    transactionManager.rollback(transactionStatus);
                } catch (Exception e) {
                    log.error("mainTask.await异常", e);
                }
            });
        });

        // 二阶段 等所有子线程执行完成
        try {
            subTask.await(timeout, unit);
            long count = subTask.getCount();
            // 主线程等待超时，子线程可能发生长时间阻塞，死锁
            if (count > 0) {
                submit.set(false);
            }
            // 所有子线程任务执行完成
            mainTask.countDown();
            return submit.get();
        } catch (Exception e) {
            log.error("subTask.await异常", e);
        }
        return submit.get();
    }
}
