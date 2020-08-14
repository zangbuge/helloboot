package com.hugmount.helloboot.util.mq;

import lombok.Data;

import java.util.ArrayList;

/**
 * @Author: Li Huiming
 * @Date: 2020/8/13
 */
@Data
public class PoolProperties {
    // 已被分配出去的连接数
    private int checkedOut;
    // 空闲池，根据创建时间顺序存放已创建但尚未分配出去的连接
    private ArrayList freeConnections;
    // 连接池里连接的最小数量
    private int minConn;
    private int maxConn;
    //为这个连接池取个名字，方便管理
    private String name;

    private String url;
    private int port;
    private String user;
    private String password;
    private String vHost;

}
