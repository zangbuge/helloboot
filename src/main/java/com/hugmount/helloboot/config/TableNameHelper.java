package com.hugmount.helloboot.config;

/**
 * @author lhm
 * @date 2023/11/8
 */
public class TableNameHelper {

    private static final ThreadLocal<String> MY_TABLE_NAME = new ThreadLocal<>();

    public static void setTableName(String tableName) {
        MY_TABLE_NAME.set(tableName);
    }

    public static String getTableName() {
        return MY_TABLE_NAME.get();
    }

}
