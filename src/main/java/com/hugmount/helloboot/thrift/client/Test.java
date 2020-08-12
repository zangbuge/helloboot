package com.hugmount.helloboot.thrift.client;

import com.alibaba.fastjson.JSON;
import com.hugmount.helloboot.thrift.server.HelloService;
import com.hugmount.helloboot.thrift.server.UserInfo;
import org.apache.thrift.TException;

/**
 * @Author: Li Huiming
 * @Date: 2020/8/12
 */
public class Test {
    public static void main(String[] args) throws TException {
        ThriftClient.init("127.0.0.1", 9800);

        Object client = ThriftClient.get(HelloService.class);
        HelloService.Client client1 = (HelloService.Client) client;
        UserInfo user = client1.getUser(12);
        System.out.println("res: " + JSON.toJSONString(user));

        HelloService.Client client2 = ThriftClient.getClient(HelloService.Client.class);
        UserInfo user1 = client2.getUser(12);
        System.out.println("data: " + user1);
    }

}
