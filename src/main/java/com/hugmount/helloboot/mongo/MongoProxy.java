package com.hugmount.helloboot.mongo;


import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.collections.IteratorUtils;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Li Huiming
 * @Date: 2020/12/16
 */
public class MongoProxy {

    private static MongoClient mongoClient;

    public void init(String host, String db, String username, String password) {


    }

    public static void main(String[] args){
        try {
            //连接到MongoDB服务 如果是远程连接可以替换“localhost”为服务器所在IP地址
            //ServerAddress()两个参数分别为 服务器地址 和 端口
            ServerAddress serverAddress = new ServerAddress("localhost",27017);
            List<ServerAddress> addrs = new ArrayList<>();
            addrs.add(serverAddress);

            //MongoCredential.createScramSha1Credential()三个参数分别为 用户名 数据库名称 密码
            MongoCredential credential = MongoCredential.createScramSha1Credential("admin", "admin", "123456".toCharArray());
            MongoClientOptions options = MongoClientOptions.builder().build();
            //通过连接认证获取MongoDB连接
            MongoClient mongoClient = new MongoClient(addrs, credential, options);
            //连接到数据库
            MongoDatabase mongoDatabase = mongoClient.getDatabase("admin");
            MongoCollection<Document> user = mongoDatabase.getCollection("user");
            FindIterable<Document> findIterable = user.find();

            MongoCursor<Document> mongoCursor = findIterable.iterator();
            List list = IteratorUtils.toList(mongoCursor);
            System.out.println(list);
            System.out.println("Connect to database successfully");
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }
}
