package com.hugmount.helloboot.thrift.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
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
                tTransport.open();
                tProtocol = new TBinaryProtocol(tTransport);
                log.info("ThriftClient init success");
            }
        } catch (TTransportException e) {
            log.error("ThriftClient init fail", e);
        }
    }

    public static <T> T getClient(Class clazz) {
        try {
            String name = clazz.getName();
            TMultiplexedProtocol tMultiplexedProtocol = new TMultiplexedProtocol(tProtocol, name);
            Class<?> client = Class.forName(name + "$Client");
            Object obj = client.getConstructor(TProtocol.class).newInstance(tMultiplexedProtocol);
            return (T) obj;

        } catch (Exception e) {
            log.error("get thrift client fail", e);
        }

        return null;
    }

}
