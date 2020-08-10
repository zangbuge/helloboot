package com.hugmount.helloboot.thrift.server.impl;

import com.hugmount.helloboot.thrift.server.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TMultiplexedProcessor;
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

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

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
        new Thread(() -> {
            TProcessor processor = new HelloService.Processor<HelloService.Iface>(new HelloServiceImpl());
            try {
//                TServerSocket serverSocket = new TServerSocket(port);
                TServerSocket serverSocket = new TServerSocket(new InetSocketAddress("0.0.0.0", port));
                TThreadPoolServer.Args args = new TThreadPoolServer.Args(serverSocket);

                // 配置传输数据的格式
                // TBinaryProtocol: 二进制格式；
                // TCompactProtocol: 压缩格式；
                // TJSONProtocol: JSON格式；
                // TSimpleJSONProtocol: 提供只写的JSON协议；
                args.protocolFactory(new TBinaryProtocol.Factory());
                // 配置数据传输的方式
                // TSocket: 阻塞式socket；
                // TFramedTransport: 以frame为单位进行传输，非阻塞式服务中使用；
                // TFileTransport: 以文件形式进行传输；
                args.transportFactory(new TTransportFactory());
                // 配置处理器用来处理
                List<TProcessor> arrayList = new ArrayList<>();
                arrayList.add(processor);
                TMultiplexedProcessor tMultiplexedProcessor = registerProcessor(arrayList);
                args.processor(tMultiplexedProcessor);
                TServer server = new TThreadPoolServer(args);
                log.info("thrift server start success, port = {}", port);
                server.serve();
            } catch (TTransportException e) {
                log.error("thrift server start fail", e);
            }
        }).start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }


    public TMultiplexedProcessor registerProcessor(List<TProcessor> tProcessors) {
        TMultiplexedProcessor tMultiplexedProcessor = new TMultiplexedProcessor();
        for (TProcessor tProcessor : tProcessors) {
            String tService = "HelloService";
            tMultiplexedProcessor.registerProcessor(tService, tProcessor);
            log.info("注册thrift处理器: {}", tService);
        }
        return tMultiplexedProcessor;
    }

}
