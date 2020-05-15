package com.hugmount.helloboot.thrift.server.impl;

import com.hugmount.helloboot.thrift.server.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.TTransportFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Author: Li Huiming
 * @Date: 2020/5/14
 */
@Component
@Slf4j
public class ThriftServer implements ApplicationContextAware {

    @Value("${thrift.port}")
    private int port;

    private static ApplicationContext context;

    public void start() {
        new Thread(){
            @Override
            public void run(){
                TProcessor processor = new HelloService.Processor<HelloService.Iface>(new HelloServiceImpl());
                try{
                    TServerSocket serverSocket = new TServerSocket(port);
                    TThreadPoolServer.Args args = new TThreadPoolServer.Args(serverSocket);
                    TBinaryProtocol.Factory protocolFactory = new TBinaryProtocol.Factory();
                    TTransportFactory transportFactory = new TTransportFactory();
                    args.protocolFactory(protocolFactory);
                    args.transportFactory(transportFactory);
                    args.processor(processor);
                    TServer server = new TThreadPoolServer(args);
                    log.info("thrift server start success, port = {}", port);
                    server.serve();
                }catch (TTransportException e){
                    log.error("thrift server start fail", e);
                }
            }
        }.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

}
