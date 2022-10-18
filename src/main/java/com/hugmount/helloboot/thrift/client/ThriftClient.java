package com.hugmount.helloboot.thrift.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.layered.TFramedTransport;

/**
 * 该类可以配置注入到spring中使用
 *
 * @Author: Li Huiming
 * @Date: 2020/8/12
 */
@Slf4j
public class ThriftClient {

    private static TProtocol tProtocol;

    public static void init(String host, int port) {
        log.info("init ThriftClient {}: {}", host, port);
        try {
            if (tProtocol == null) {
                TTransport tTransport = new TSocket(host, port);
                // 数据传输方式要和服务端一致
                TFramedTransport tFramedTransport = new TFramedTransport(tTransport);
                tProtocol = new TBinaryProtocol(tFramedTransport);
                tTransport.open();
                log.info("ThriftClient init success");
            }
        } catch (TTransportException e) {
            log.error("ThriftClient init fail", e);
        }
    }

    public static <T> T getClient(Class<T> clientClass) {
        try {
            String name = clientClass.getCanonicalName();
            String service = name.substring(0, name.lastIndexOf("."));
            return (T) get(Class.forName(service));
        } catch (Exception e) {
            log.error("get thrift client fail", e);
        }

        return null;
    }

    public static Object get(Class serviceClazz) {
        try {
            String name = serviceClazz.getName();
            TMultiplexedProtocol tMultiplexedProtocol = new TMultiplexedProtocol(tProtocol, name);
            Class<?> client = Class.forName(name + "$Client");
            Object obj = client.getConstructor(TProtocol.class).newInstance(tMultiplexedProtocol);
            return obj;

        } catch (Exception e) {
            log.error("get thrift client fail", e);
        }

        return null;
    }

}
