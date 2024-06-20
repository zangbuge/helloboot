package com.hugmount.helloboot.util;

import lombok.extern.slf4j.Slf4j;

/**
 * @author lhm
 * @date 2024/6/19
 */
@Slf4j
public class PrintUtil {

    public static synchronized String addUseTimeLog(String msg, Task task) {
        long start = System.currentTimeMillis();
        try {
            return task.run();
        } catch (Exception e) {
            throw e;
        } finally {
            log.info(msg + " 耗时: {}ms", System.currentTimeMillis() - start);
        }
    }

    public interface Task {
        String run();
    }

}
