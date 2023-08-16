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
方式一
mvn dependency:tree -Dincludes=org.apache.httpcomponents

方式二
安装 Maven Helper 插件,点击pom.xml,左下边显示多了个“Dependency Analyzer”Tab选项, 选择 "all as list" 搜索


优秀github项目
国产 Star 破 10w+ 的开源项目，前端包括管理后台 + 微信小程序，后端支持单体和微服务架构。
功能涵盖 RBAC 权限、SaaS 多租户、数据权限、商城、支付、工作流、大屏报表、微信公众号等等功能：
github地址: https://github.com/YunaiV/ruoyi-vue-pro
视频教程：https://doc.iocoder.cn
