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

    private TBinaryProtocol.Factory protocolFactory;

    private TTransportFactory transportFactory;

    private static ApplicationContext context;

    public void init() {
        if (null == protocolFactory)
            protocolFactory = new TBinaryProtocol.Factory();

        if (null == transportFactory)
        transportFactory = new TTransportFactory();
    }

    public void start() {
        new Thread(){
            @Override
            public void run(){
                TProcessor processor = new HelloService.Processor<HelloService.Iface>(new HelloServiceImpl());
                init();
                try{
                    TServerSocket serverSocket = new TServerSocket(port);
                    TThreadPoolServer.Args args = new TThreadPoolServer.Args(serverSocket);
                    args.protocolFactory(protocolFactory);
                    args.processor(processor);
                    args.transportFactory(transportFactory);
                    TServer server = new TThreadPoolServer(args);
                    log.info("thrift server start success, port={}",port);
                    server.serve();
                }catch (TTransportException e){
                    log.error("thrift server start fail",e);
                }
            }
        }.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

}
