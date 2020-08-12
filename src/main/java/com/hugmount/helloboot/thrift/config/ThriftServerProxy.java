package com.hugmount.helloboot.thrift.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportFactory;

import java.net.InetSocketAddress;
import java.util.*;

/** thrift代理
 * @Author: Li Huiming
 * @Date: 2020/8/11
 */
@Slf4j
@Data
public class ThriftServerProxy {

    private int port;

    @Data
    public static class Processor {

        private String serviceInterface;

        private Object serviceImplObject;
    }

    private List<Processor> processors;


    public void start() {
        new Thread(() ->  run()).start();
    }

    public void run() {
        try {
            TServerSocket tServerSocket = new TServerSocket(new InetSocketAddress("0.0.0.0", port));
            TThreadPoolServer.Args args = new TThreadPoolServer.Args(tServerSocket);
            args.protocolFactory(new TBinaryProtocol.Factory());
            args.transportFactory(new TTransportFactory());
            TMultiplexedProcessor tMultiplexedProcessor = registerProcessor(getTProcessorList());
            args.processor(tMultiplexedProcessor);
            TServer server = new TThreadPoolServer(args);
            log.info("thrift server ThriftServerProxy start success, port = {}", port);
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Map<String, TProcessor>> getTProcessorList() throws Exception {
        List<Map<String, TProcessor>> arrayList = new ArrayList<>();
        Map<String, TProcessor> map = new HashMap<>();
        for (Processor processor : processors) {
            String serviceInterface = processor.getServiceInterface();
            Object serviceImplObject = processor.getServiceImplObject();
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
