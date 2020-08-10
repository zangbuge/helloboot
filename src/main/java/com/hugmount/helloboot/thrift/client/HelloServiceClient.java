package com.hugmount.helloboot.thrift.client;

import com.alibaba.fastjson.JSON;
import com.hugmount.helloboot.thrift.server.HelloService;
import com.hugmount.helloboot.thrift.server.UserInfo;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * @Author: Li Huiming
 * @Date: 2020/5/14
 */
public class HelloServiceClient {
    public static void main(String[] args) {
        try {
            TTransport tTransport = new TSocket("127.0.0.1", 9899);
            tTransport.open();
            TProtocol protocol = new TBinaryProtocol(tTransport);
            TMultiplexedProtocol multiplexedProtocol = new TMultiplexedProtocol(protocol,"HelloService");
            HelloService.Client client = new HelloService.Client(multiplexedProtocol);
            UserInfo user = client.getUser(12);
            System.out.println("调用结果: " + JSON.toJSONString(user));
            tTransport.close();
        } catch (TTransportException e) {
            e.printStackTrace();
        }catch (TException e){
            e.fillInStackTrace();
        }

    }

}
