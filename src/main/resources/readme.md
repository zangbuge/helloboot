##### 一. windows安装mongo
1.下载
http://dl.mongodb.org/dl/win32/x86_64
选择版本: win32/mongodb-win32-x86_64-2012plus-v4.2-latest.zip
2.配置系统环境变量
3.mongodb目录下新建一个mongod.cfg
```$xslt
#日志输出文件
logpath =E:\mongodb\logs\mongodb.log
#数据存放文件夹
dbpath =E:\mongodb_data
```
4.手动安装mongo \bin 目录下 执行 
```$xslt
mongod.exe --config "D:\work\tool\mongo\mongodb-win32-x86_64-2012plus-4.2.11\mongod.cfg" --install
```
5.启动服务 net start mongodb
6.关闭服务 net stop mongodb
7.打开客户端默认是链接到test数据库无需用户名密码
8.\bin 目录下 执行mongo 进入操作mongo数据库
9.创建数据库 use admin
10.创建用户, 只能手动写入命令, 执行后出现 successful added user:{...} 即成功
```$xslt
db.createUser({user:'admin',pwd:'123456',customData:{"desc":"administrators"},roles:[{role:'userAdminAnyDatabase',db:'admin'}]})

```

##### 二. maven查看某个jar是被谁引入进来的 
mvn dependency:tree -Dincludes=org.apache.httpcomponents

