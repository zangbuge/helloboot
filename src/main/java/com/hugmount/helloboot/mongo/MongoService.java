package com.hugmount.helloboot.mongo;

import com.alibaba.fastjson.JSON;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

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

        List<User> find = datastore.find(User.class).asList();
        System.out.println(JSON.toJSONString(find));

        Query<User> query = datastore.createQuery(User.class);
        query.filter("child.tel", "123");
        List<User> userList = query.asList();
        System.out.println(JSON.toJSONString(userList));

        // 修改addr 条件query
        UpdateOperations<User> updateOperations = datastore.createUpdateOperations(User.class);
        updateOperations.set("addr","宋庄");
        datastore.update(query, updateOperations);

        User user = new User();
        user.setId("add123");
        user.setName("test");
        user.setAddr("保存 或根据id全更新");
        datastore.save(user);

//        datastore.delete(query);

    }
}
