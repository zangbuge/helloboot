package com.hugmount.helloboot.mongo;

import cn.hutool.json.JSONUtil;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import lombok.extern.slf4j.Slf4j;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @Author: Li Huiming
 * @Date: 2020/12/17
 */
@Slf4j
@Service
public class MongoService {

    @Value("${mongo.host}")
    private String host;

    @Value("${mongo.port}")
    private int port;

    @Value("${mongo.username}")
    private String username;

    @Value("${mongo.password}")
    private String password;

    @Value("${mongo.database}")
    private String database;

    private static Datastore datastore;

    @Bean
    protected Datastore datastore() {
        String host = this.host;
        int port = this.port;
        String username = this.username;
        String password = this.password;
        String database = this.database;
        return getDatastore(host, port, username, password, database);
    }

    public Datastore getDatastore(String host, int port, String username, String password, String database) {
        String[] split = host.split(",");
        List<ServerAddress> addressList = new LinkedList<>();
        for (String ip : split) {
            ServerAddress serverAddress = new ServerAddress(ip, port);
            addressList.add(serverAddress);
        }
        MongoCredential credential = MongoCredential.createCredential(username, database, password.toCharArray());

        MongoClientOptions.Builder builder = MongoClientOptions.builder();
        builder.connectTimeout(6000);
        builder.maxWaitTime(6000); // 线程阻塞等待最长时间
        builder.connectionsPerHost(8); // 最大连接数
        builder.minConnectionsPerHost(2); // 每个主机的最小连接数
        builder.maxConnectionIdleTime(1000 * 60 * 5); // 池连接的最大空闲时间
        MongoClientOptions options = builder.build();
        MongoClient mongoClient = new MongoClient(addressList, credential, options);

        Morphia morphia = new Morphia();
        datastore = morphia.createDatastore(mongoClient, database);
        // 为实体类创建索引
        datastore.ensureIndexes();
        log.info("mongo connection init success");
        return datastore;
    }

    public <T> T queryOne(String where, Object val, Class<T> tClass) {
        List<T> ts = queryList(where, val, tClass);
        if (CollectionUtils.isEmpty(ts)) {
            return null;
        }
        return ts.get(0);
    }

    public <T> List<T> queryList(String where, Object val, Class<T> tClass) {
        Query<T> query = datastore.createQuery(tClass);
        return query.filter(where, val).asList();
    }

    public <T> void update(String where, Object val, Map<String, Object> map, Class<T> tClass) {
        Query<T> query = datastore.createQuery(tClass);
        query.filter(where, val);
        UpdateOperations<T> updateOperations = datastore.createUpdateOperations(tClass);
        Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> next = iterator.next();
            updateOperations.set(next.getKey(), next.getValue());
        }
        datastore.update(query, updateOperations);
    }

    public <T> void save(T t) {
        datastore.save(t);
    }


    public static void main(String[] args) {
        String host = "192.168.38.130";
        int port = 27017;
        String username = "admin";
        String password = "123456";
        String database = "admin";
        MongoService mongoService = new MongoService();
        Datastore datastore = mongoService.getDatastore(host, port, username, password, database);
        // 为实体类创建索引
        datastore.ensureIndexes();

        User user = new User();
        user.setId("add123");
        user.setName("lhmtest");
        user.setAddr("保存 或根据id全更新");
        Map<String, Object> map = new HashMap<>();
        map.put("tel", "1589");
        user.setChild(map);
        datastore.save(user);

        List<User> find = datastore.find(User.class).asList();
        System.out.println("所有数据" + JSONUtil.toJsonStr(find));

        Query<User> query = datastore.createQuery(User.class);
        query.filter("child.tel", "1589");

        // 修改addr 条件query
        UpdateOperations<User> updateOperations = datastore.createUpdateOperations(User.class);
        updateOperations.set("addr", "修改addr");
        UpdateResults update = datastore.update(query, updateOperations);
        System.out.println("修改成功" + JSONUtil.toJsonStr(update));

        List<User> userList = query.asList();
        System.out.println("条件查询结果: " + JSONUtil.toJsonStr(userList));

//        datastore.delete(query);

    }
}
