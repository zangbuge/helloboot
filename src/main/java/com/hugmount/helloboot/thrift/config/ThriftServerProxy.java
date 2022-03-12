package com.hugmount.helloboot.thrift.config;

import com.hugmount.helloboot.util.ThreadUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TServerSocket;

import java.net.InetSocketAddress;
import java.util.*;

/**
 * thrift代理
 *
 * @Author: Li Huiming
 * @Date: 2020/8/11
 */
@Slf4j
@Data
public class ThriftServerProxy {

    private int port;

    @Data
    public static class Processor {

        private Class<?> serviceInterface;

        private Object serviceImplObject;
    }

    private List<Processor> processors;


    public void start() {
        ThreadUtil.execute(() -> run());
    }

    public void run() {
        try {
            TServerSocket tServerSocket = new TServerSocket(new InetSocketAddress("0.0.0.0", port));
            TThreadPoolServer.Args args = new TThreadPoolServer.Args(tServerSocket);
            // 设置数据传输协议（TBinaryProtocol默认二进制编码格式） TJSONProtocol 使用JSON的数据编码协议进行数据传输
            args.protocolFactory(new TBinaryProtocol.Factory());
            // 设置传输方式 TSocket 阻塞的io  TFramedTransport 非阻塞io
            args.transportFactory(new TFramedTransport.Factory());
            TMultiplexedProcessor tMultiplexedProcessor = registerProcessor(getProcessorList());
            args.processor(tMultiplexedProcessor);
            // TSimpleServer  单线程 阻塞
            // TThreadPoolServer  多线程  阻塞
            // TNonblockingServer  多线程  非阻塞
            TServer server = new TThreadPoolServer(args);
            log.info("thrift server ThriftServerProxy start success, port = {}", port);
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Map<String, TProcessor>> getProcessorList() throws Exception {
        List<Map<String, TProcessor>> arrayList = new ArrayList<>();
        Map<String, TProcessor> map = new HashMap<>();
        for (Processor processor : processors) {
            String serviceInterface = processor.getServiceInterface().getCanonicalName();
            Object serviceImplObject = processor.getServiceImplObject();
            // $Processor 获取内部类
            Class<?> processClass = Class.forName(serviceInterface + "$Processor");
            Class iface = Class.forName(serviceInterface + "$Iface");
            TProcessor tProcessor = (TProcessor) processClass.getDeclaredConstructor(iface).newInstance(serviceImplObject);
            map.put(serviceInterface, tProcessor);
            arrayList.add(map);
        }

        return arrayList;
    }


    public TMultiplexedProcessor registerProcessor(List<Map<String, TProcessor>> tProcessors) {
        TMultiplexedProcessor tMultiplexedProcessor = new TMultiplexedProcessor();
        for (Map<String, TProcessor> tProcessor : tProcessors) {
            Iterator<Map.Entry<String, TProcessor>> iterator = tProcessor.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, TProcessor> next = iterator.next();
                String tService = next.getKey();
                tMultiplexedProcessor.registerProcessor(tService, next.getValue());
                log.info("已注册thrift处理器: {}", tService);
            }
        }
        return tMultiplexedProcessor;
    }


}
