package com.hugmount.helloboot.mongo;

import com.alibaba.fastjson.JSON;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author: Li Huiming
 * @Date: 2020/12/17
 */
public class MongoService {
    public static void main(String[] args) {
        List<ServerAddress> addressList = new LinkedList<>();
        ServerAddress serverAddress = new ServerAddress("localhost", 27017);
        addressList.add(serverAddress);
        MongoCredential credential = MongoCredential.createCredential("admin", "admin", "123456".toCharArray());

        MongoClientOptions.Builder builder = MongoClientOptions.builder();
        builder.connectTimeout(9000);
        builder.maxWaitTime(6000);
        MongoClientOptions options = builder.build();
        MongoClient mongoClient = new MongoClient(addressList, credential, options);

        Morphia morphia = new Morphia();
        Datastore datastore = morphia.createDatastore(mongoClient, "admin");
        // 为实体类创建索引
        datastore.ensureIndexes();

        List<User> users = datastore.find(User.class).asList();
        System.out.println(JSON.toJSONString(users));

    }
}
