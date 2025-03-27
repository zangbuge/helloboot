参考资料
http://c.biancheng.net/view/3118.html
https://docs.docker.com/engine/reference/commandline/run/
docker官方仓库: https://hub.docker.com/

环境
腾讯云 centos v7.6

条件
要求linux内核版本高于3.10
uname -r  #查看centos内核版本

yum方式安装
yum install -y yum-utils device-mapper-persistent-data lvm2   #安装docker所需的依赖软件包
yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo  #使用阿里云镜像
yum makecache fast			#生成缓存将软件包信息提前在本地缓存一份，用来提高搜索安装软件的速度
yum -y install docker-ce 使用yum安装社区版本 或制定版本 docker-ce-19.03.1
systemctl enable docker  让docker加入开机启动
systemctl restart docker 启动docker
yum -y remove docker-ce  卸载docker
yum -y remove docker-ce-cli 


查看版本
docker version

用户加入docker组
sudo usermod -aG docker lhmuser #不用使用sudo

常用命令
docker info   查看信息
docker images 查看docker的本地镜像
docker ps     查看正在运行的容器
docker ps -a  列出所有容器包含未运行的
docker build  根据Dockerfile打包镜像
docker pull   拉取远程仓库镜像
docker pull jenkins/jenkins  #下载某用户镜像 用户名/镜像名
docker run    运行容器
docker stop xxx  停止容器 xxx为运行容器ID
docker image history boot-docker-intro  查看镜像构建历史
docker rm 删除容器 
docker rmi 删除镜像 
docker rmi jenkins/jenkins:2.340  删除镜像根据 repository:tag
docker search 在Docker Hub中查找镜像 
docker load -i dm8_20230808_rev197096_x86_rh6_64_single.tar docker导入离线安装包
-d: 后台运行容器，并返回容器ID； 
-name=”nginx-lb”: 为容器指定一个名称； 

Docker这些none:none的镜像
docker images -a 有一堆<none>:<none>的镜像,可分为两类：好与坏、有用与无用
好的<none>:<none>镜像是由于镜像分层的中间镜像。
它们只会在docker images -a才会显示出来，用docker images是不会显示的。它们也不会造成空间问题。
坏的<none>:<none>镜像会占用空间，(重复build)主要是由于新加镜像替换原来相对标签的镜像，
原来镜像就变成了<none>:<none>而不会被删除, 这些坏的镜像也叫 dangling images
Docker没有自动删除这些镜像的机制，可以通过以下命令删除：
docker rmi $(docker images -f "dangling=true" -q)
检查一下是否还有<none>的镜像
docker images | grep '<none>'

docker创建虚拟网卡 默认是bridge方式, 会有一个docker0的虚拟网卡
在日常的生产环境中，docker0已经很少被使用了，因为它不支持使用容器服务名称进行通信，推荐使用自定义网络
自定义网络
我们可以通过命令 docker network create --driver bridge --subnet 192.168.0.0/16 --gateway 192.168.0.1 mynet 创建自定义网络mynet
--driver bridge指定使用网络的类型，可以选择none，host，bridge任何一种；
--subnet 192.168.0.0/16指定子网的范围；
--gateway 192.168.0.1指定网关的地址；

docker network ls 查看

docker架构3个部分
client      客户端执行命令
dockerhost  包含containers, images
registry    注册中心, 镜像仓库,类似maven仓库

快速启动容器 -p 表示映射主机8880端口到docker的80端口
docker run --rm --name myNginx -p 8880:80 nginx  #本地没有镜像则去远程仓库下载该镜像
很可能无法正常拉取镜像，所以就需要我们为docker设置国内阿里云的镜像加速器
需要修改配置文件后,docker必须重启 vi /etc/docker/daemon.json
{
    "registry-mirrors": [
        "https://docker.211678.top",
        "https://docker.1panel.live",
        "https://hub.rat.dev",
        "https://docker.m.daocloud.io",
        "https://do.nark.eu.org",
        "https://dockerpull.com",
        "https://dockerproxy.cn",
        "https://docker.awsl9527.cn"
    ]
}

启动后可以通过主机8880端口访问nginx了
启动一个容器, 即创建一个linux系统,里运行着nginx

进入容器  -it 表示产生一个交互式终端, 和容器进行命令交互
docker exec -it 32434werwer bash   it后面跟容器的Id

进入容器中ngxin目录
cd usr/share/nginx/

远程中心镜像地址, 搜索nginx 点击containers 即可看到docker中所有nginx支持的镜像
https://hub.docker.com  

#### Dockerfile
创建 hellodocker 文件夹
文件夹下创建 index.html
<html>hello docker</html>

再创建Dockerfile文件  文件名必须是Dockerfile 没有后缀, 内容为: 
FROM nginx
COPY ./index.html /usr/share/nginx/html/index.html
EXPOSE 80

进入hellodocker文件夹执行命令
docker image build ./ -t hello-docker:1.0.0 # 打包镜像

创建容器及启动容器
docker container create -p 2333:80 hello-docker:1.0.0
docker container start xxx # xxx 为上一条命令运行得到的结果

访问主机 127.0.0.1:2333 应该能看到刚刚自己写的index.html内容

当容器运行后，可以通过如下命令进入容器内部
docker container exec -it xxx /bin/bash # xxx 为容器ID
-it 等在容器内的命令执行完毕才会出来到当前操作,而bash 是打开容器内的一个终端近程,又因为it的等待 
所以就会一直以终端连接的方式停留在容器内部

#nginx配置log打印格式, nginx.conf 的log_format 指令
$remote_addr： 用以记录客户端的ip地址；
$remote_user： 用来记录客户端用户名称；
$time_local：  用来记录访问时间与时区；
$request：     用来记录请求的http的方式与url；
$request_time：用来记录请求花费的时间；
$status：      用来记录请求状态；成功是200
$body_bytes_sent：    记录发送给客户端文件主体内容大小；
$http_referer：       用来记录从那个页面链接访问过来的;
$http_user_agent：    记录客户浏览器的相关信息,对应的是 "Mozilla/5.0 (iPhone; CPU iPhone...";
$http_x_forwarded_for:代理IP,记录经过的所有服务器的IP地址;

#nginx获取请求url中的参数, 在nginx配置中，通过$arg_PARAMETER 即可获得请求PARAMETER的内容
如: http://localhost/test?call_url=http://127.0.0.1:8030/test/callbackTest
取值, 转到配置地址
proxy_pass "$arg_call_url";


#### docker安装mysql
docker pull mysql:5.7  #拉取docker mysql官方镜像 或 docker pull mysql:latest  最新的
docker images          
#启动mysql
docker run -d --restart=always --name mysql57 -p 3306:3306 -e TZ="Asia/Shanghai" -v $PWD/mysql/conf:/etc/mysql/conf.d -v $PWD/mysql/db:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=123456 mysql:5.7 --lower_case_table_names=1

MYSQL_ROOT_PASSWORD=123456：设置 MySQL 服务 root 用户的密码 即可登录mysql
lower_case_table_names=1  #表示数据库不区分大小写, 否则表名不一致会报错

进入容器
docker exec -it mysql57 bash
登录mysql
mysql -u root -p
修改密码
ALTER USER 'root'@'localhost' IDENTIFIED BY '123456';
添加远程登录用户
CREATE USER 'lhm'@'%' IDENTIFIED WITH mysql_native_password BY '123456';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%';

查看用户
select user, host from mysql.user;

运行mysql8
docker run --name mysql8 -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=123456 -v /home/mysql/logs:/logs -v /home/mysql/data:/var/lib/mysql mysql:8
docker exec -it mysql8 /bin/bash
mysql -uroot -p123456
	use mysql;
	ALTER USER 'root'@'%' IDENTIFIED BY '123456' PASSWORD EXPIRE NEVER;
	ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY '123456';
	FLUSH PRIVILEGES;

重启容器

mysql -h 192.168.184.6 -u root -P3306 -p
show databases; 查看数据库
use databases; 进入数据库
show tables; 查看当前所在数据库下面的所有表
select * from test; #必须加分号;
desc test1; # 查看表格的字段信息
show create table test1; # 查询指定表的建表语句ddl 

mysql严格模式
MySQL自身对数据进行严格的校验(格式、长度、类型等)，比如一个整型字段我们写入一个字符串类型的数据，
在非严格模式下MySQL不会报错, 定义的类型长度超出了也不会报错, 严格模式则会

STRICT_TRANS_TABLES : 严格模式
NO_ENGINE_SUBSTITUTION : 无引擎提交,create table 时指定的 engine 项不被支持, 这个时候 MySQL 会报错
PAD_CHAR_TO_FULL_LENGTH : 填补字符到全长度

5.6版本默认没有开启严格模式
5.7版本及以上或者开启了严格模式, 一旦超出范围立刻报错(Data too long for column ‘name’ at row 1)
查看当前模式
select @@sql_mode;
show variables like "%mode";  # 或者, 匹配含有 mode 字符的变量
临时设置
set session sql_mode='[模式]'; : 只对当前连接有效(可以省略 session)
set global sql_mode='[模式]'; : 对全局有效(所有连接)
set sql_mode='STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION';  # 开启严格模式+无引擎提交

永久设置
Linux 配置文件 my.cnf
sql-mode="STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION"  # 将其配置在 [mysqld] 下面

查看版本
select version();
select uuid();
字符集
show VARIABLES LIKE 'character%';

临时设置
show variables like'%time_zone';
set global time_zone = '+8:00';

刷新mysql
FLUSH PRIVILEGES;

永久配置
修改配置文件my.ini
default-time_zone = '+8:00'
default-character-set=utf8mb4


#### 打包springboot应用
Dockerfile文件如下

FROM maven:3.5.2-jdk-8-alpine AS MAVEN_BUILD
MAINTAINER Brian Hannaway
COPY pom.xml /build/
COPY src /build/src/
WORKDIR /build/
RUN mvn package
FROM openjdk:8-jre-alpine
WORKDIR /app
COPY --from=MAVEN_BUILD /build/target/helloboot-0.0.1-SNAPSHOT.jar /app/
ENTRYPOINT ["java", "-jar", "helloboot-0.0.1-SNAPSHOT.jar"]

docker image build ./ -t helloboot:0.0.1   #打包镜像  时间会比较长或30分钟
docker container run -p 8086:8086 helloboot:0.0.1    #启动应用

docker logs -f -t --tail 100 datacenterId  #查看容器启动项目的日志
-f 跟踪实时日志 --follow
-t 显示时间戳
100 只显示最后100行

docker logs --since 30m CONTAINER_ID #查看最近30分钟的日志

# 进入到应用容器内 容器尚未bash安装  使用bash进入会出现错误: starting container process caused: exec: "bash": executable file not found in $PATH: unknown
docker exec -it helloboot sh


### centos7.9安装 Node.js v16
官网下载 (选择16版本，更新版本需要升级glib，比较麻烦)
地址一 https://nodejs.org/dist/v20.15.1/
地址二 https://nodejs.org/download/release/v16.14.1/node-v16.14.1-linux-x64.tar.xz
tar -xvf node-v16.14.1-linux-x64.tar.xz
-- 软连接方式部署bin文件(卸载直接删除文件/usr/bin/node)
ln -s /home/node/node-v16.14.1-linux-x64/bin/node /usr/bin/node
ln -s /home/node/node-v16.14.1-linux-x64/bin/npm /usr/bin/npm
-- 验证安装成功
node -v
npm -v
#### 安装mongo
docker run --name mymongo -d -p 27017:27017 -v /data/configdb:/data/configdb -v /data/mongo:/data/db mongo:5.0.6
docker exec -it mymongo mongo admin  #进入mongo命令
db.createUser({ user:'admin',pwd:'123456',roles:[ { role:'root', db: 'admin'}]})  #创建用户

#### 推送镜像到远程仓库
docker login -u 用户名  #登录到官方Docker Hub仓库
必须先打一个tag再推送
docker tag helloboot:0.0.1 zangbuge/helloboot
docker push zangbuge/helloboot
docker logout 退出登录

#### 安装jenkins
# -v /var/jenkins_mount:/var/jenkins_home 挂载目录
# -v /etc/localtime:/etc/localtime让容器使用和服务器同样的时间设置
# -v /var/run/docker.sock:/var/run/docker.sock 与 -v /usr/bin/docker:/usr/bin/docker是把宿主机docker 映射到容器内
mkdir -p /var/jenkins_mount  #先创建一个jenkins工作目录 
sudo chmod -R 777 /var/jenkins_mount  #设置权限 -R 指级联应用到目录里的所有子目录和文件, 777 是所有用户都拥有最高权限
sudo chmod 777 /var/run/docker.sock   #在容器内构建docker权限仅一次有效, 机器重启后失效. 
# --privileged=true使docker容器内拥有真正的root权限,允许在docker容器之中再启动docker容器,--user=root 以root权限启动容器
docker run --user=root --privileged=true  --name myjenkins -d -p 18080:8080 -p 50000:50000 -e TZ="Asia/Shanghai" -v /var/jenkins_mount:/var/jenkins_home -v /var/run/docker.sock:/var/run/docker.sock -v /usr/bin/docker:/usr/bin/docker jenkins/jenkins:2.357
访问jenkins地址 18080
vi /var/jenkins_mount/secrets/initialAdminPassword  #Jenkins密码位置
docker logs myjenkins  #查看docker容器日志

jenkins中全局配置git环境 
whereis git  #查看git安装路径
配置git路径为 /usr/bin/git
#进入容器查看所属用户
ls -la /var/jenkins_home
#查看用户名的uid gid
id jenkins
#添加用户
groupadd jenkins
useradd jenkins -g jenkins
# 查看端口占用情况 
netstat -tunlp |grep 18080


#### centos安装jenkins
#先安装jdk(方式一), jenkins是基于java环境的, 查看安装的路径 which java
yum -y install java-1.8.0-openjdk
卸载jdk
yum list installed | grep java
yum -y remove java-1.8.0*
yum -y remove javapackages-tools.noarch
yum -y remove python-javapackages.noarch
yum -y remove tzdata-java.noarch

#先配置jdk环境变量(方式二推荐),
国内镜像地址
http://www.codebaoku.com/jdk/jdk-oracle-jdk1-8.html
vi /etc/profile
export JAVA_HOME=/home/jdk/jdk1.8.0_331
export PATH=$PATH:$JAVA_HOME/bin

#配置maven环境
vi /etc/profile
export M2_HOME=/home/maven/apache-maven-3.8.6
export PATH=$PATH:$M2_HOME/bin
#刷新配置
source /etc/profile

#下载源文件(官网极慢) 使用阿里镜像
wget https://mirrors.aliyun.com/jenkins/redhat/jenkins-2.355-1.1.noarch.rpm
#安装
rpm -ivh jenkins-2.355-1.1.noarch.rpm
#修改端口 JENKINS_PORT="8080"
vi /etc/sysconfig/jenkins 
设置jenkins和系统时间一致, 新增配置
JAVA_ARGS="-Dorg.apache.commons.jelly.tags.fmt.timeZone=Asia/Shanghai -Dfile.encoding=UTF-8 -Djava.awt.headless=true"
#将Jenkins用户添加到根组, 解决安装插件时"无法连接到Jenkins"问题
sudo usermod -a -G root jenkins
#启动服务
service jenkins start
# 若失败,查看失败原因
systemctl status jenkins.service
#查看admin密码
vi /var/lib/jenkins/secrets/initialAdminPassword

#jenkins启动方式二, 也可在官网下载jenkins.war放在tomcat中运行
#配置tomcat时区 
vi catalina.sh
JAVA_OPTS = "-Duser.timezone=GMT+08"
#设置系统时间与网络时间同步
yum -y install ntp ntpdate
ntpdate cn.pool.ntp.org
#写入硬件, 防止重启后失效
hwclock --systohc 

#安装git  然后jenkins中配置git
sudo yum install -y git
git version
whereis git

### 设置docker权限
#否则可能出现错误: dial unix /var/run/docker.sock: connect: permission denied
chmod 666 /var/run/docker.sock  #docker每重启一次,都要设置一次

登陆jenkins后创建项目后只需在 源码管理 中配置项目git地址
然后在构建中添加shell脚本
```aidl
sh /var/lib/jenkins/workspace/helloboot/src/main/resources/sh/docker.sh
```

#彻底卸载jenkins
service jenkins stop
yum -y remove jenkins
rm -rf /var/cache/jenkins
rm -rf /var/lib/jenkins


### nacos
docker pull nacos/nacos-server
#创建自定义网络（用于容器通讯）
docker network create common-network 
#查看网络
docker network ls
#启动 nacos   
--restart=always 设置重启docker时也重启nacos 
docker update –restart = no <container_id> 更新容器的–restart标志.关闭
-itd 设置nacos一直运行, nacos在docker容器运行必须有一个前台进程， 如果没有前台进程执行，容器认为空闲，就会自行退出
docker run -itd --name nacos --restart=always --network common-network --env MODE=standalone \
-p 8848:8848 -p 9848:9848 -p 9849:9849 nacos/nacos-server:2.0.3 
#管理界面 默认端口号是：8848
http://127.0.0.1:8848/nacos/
#默认账号密码：nacos/nacos

### 指定mysql配置单机启动  MODE 默认cluster 集群模式, 容器内访问宿主机不能用 127.0.0.1, 集群启动需指定网段和宿主机一致
```aidl
docker run -d \
-e MODE=standalone \
-e SPRING_DATASOURCE_PLATFORM=mysql \
-e MYSQL_SERVICE_HOST=www.fxitalk.com \
-e MYSQL_SERVICE_PORT=3306 \
-e MYSQL_SERVICE_USER=root \
-e MYSQL_SERVICE_PASSWORD=123456 \
-e MYSQL_SERVICE_DB_NAME=nacos_config \
-p 8848:8848 \
-p 9848:9848 \
-p 9849:9849 \
--restart=always \
--name mynacos \
-v /root/nacos/logs/:/home/nacos/logs \
nacos/nacos-server:2.0.3
```

### 安装redis  命令中心:  http://www.redis.cn/commands.html
windows版 https://github.com/tporadowski/redis/releases
客户端:   https://github.com/caoxinyu/RedisClient/releases
#快捷启动
docker run -d --name redis-test -p 6379:6379 redis:6.2.7
#制定配置启动
#宿主机下添加配置文件 /data/redis/conf/redis.conf
docker run -d -p 6379:6379 --rm --name myredis \
-v /data/redis/conf:/etc/redis/conf \
-v /data/redis/db:/data \
redis:6.2.7 \
redis-server /etc/redis/conf/redis.conf
# redis-server 指定启动加载容器内的配置文件,项目中的配置redis.conf 是基于官方v6.06 

docker exec -it redis bash 【进入容器】
redis-cli 【连接,默认本地】
auth 123456 【登录】
select 1 【不执行,默认选择数据库0, 集群不能选择, 只能是0】
set hello world
get hello
ttl hello 查有效期
#默认安装目录/usr/local/bin, --raw 支持中文
redis-cli -h www.fxitalk.com -p 6379 -a 123456 --raw 

hash操作
hset lhm age 20
hget lhm age
查看map数量: hlen myhash
查看map所有值: hgetall lhm
查看list数量: llen test_list

set操作
sadd hello lhm
查看set数量: scard myset

### 安装apollo　文档：　https://www.apolloconfig.com/#/zh/deployment/quick-start　
1. 下载官方压缩包: https://github.com/apolloconfig/apollo/releases/tag/v1.9.2
　　下载２个DB脚本:　　https://github.com/apolloconfig/apollo-build-scripts/tree/master/sql
    并新建２个数据库ApolloConfigDB，ApolloPortalDB
config：提供配置的读取、推送等功能;
admin： 提供配置的修改、发布等功能;
portal：后台配置管理页面; 需要修改apollo-env.properties 配置环境, 先只保留local和dev其他注释掉
apollo-env.properties 集群meta地址逗号分隔, 或换成eureka地址
```aidl
local.meta=http://localhost:8080
dev.meta=http://localhost:8080
```
2. 解压后修改config下数据源的配置 application-github.properties
依次启动
java -jar apollo-configservice-1.9.2.jar ./config/application-github.properties
java -jar apollo-adminservice-1.9.2.jar  ./config/application-github.properties
java -jar apollo-portal-1.9.2.jar ./config/application-github.properties
3. 访问页面: http://localhost:8070
登录默认账号：apollo
登录默认密码：admin


Kuboard 
https://kuboard.cn/install/install-k8s.html#kuboard-spray
https://blog.51cto.com/u_890909/2540060

###安装Harbor 依赖docker-compose
curl -L https://get.daocloud.io/docker/compose/releases/download/1.25.5/docker-compose-`uname -s`-`uname -m` > /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose  # 添加权限
docker-compose -v 命令查看安装情况
下载Harbor安装包:　https://github.com/goharbor/harbor/releases
tar xvf harbor-offline-installer-v2.3.2.tgz
修改配置文件 地址,端口和密码
cp harbor.yml.tmpl harbor.yml
vim harbor.yml
hostname: www.fxitalk.com
harbor_admin_password: 123456  # 默认: Harbor12345
默认是https协议, 必须注释掉https相关配置 
安装 
./install.sh
启动成功标志打印:　Harbor has been installed and started successfully.
登录地址:　http://www.fxitalk.com:9980
输入用户名 admin 密码: Harbor12345

解压后的harbor目录执行命令
# 启动Harbor容器
docker-compose start
# 停止Harbor容器
docker-compose stop
# 重启Harbor容器
docker-compose restart
# 停止并删除Harbor容器，加上-v参数可以同时移除挂载在容器上的目录
docker-compose down
# 创建并启动Harbo容器，参数“-d”表示后台运行命令
docker-compose up -d

设置docker私服 docker login 支持http
vim /etc/docker/daemon.json
{
"registry-mirrors": ["https://alzgoonw.mirror.aliyuncs.com"]
,"insecure-registries": ["http://www.fxitalk.com:9980"]
,"live-restore": true
}
配置完成以后使用命令刷新生效
systemctl daemon-reload
systemctl restart docker
docker-compose start

先在harbor上创建fxitalkservice项目
docker login http://www.fxitalk.com:9980 -u zangbuge -p Harbor.123
docker tag fxitalkservice:0.0.1 www.fxitalk.com:9980/fxitalkservice/fxitalkservice:0.01
docker push www.fxitalk.com:9980/fxitalkservice/fxitalkservice:0.01


### 安装 flowable-ui  https://blog.csdn.net/agonie201218/article/details/122451371
docker run -d --name myflowableui -p 28080:8080 flowable/flowable-ui:6.7.2
默认账号:admin 密码：test  选择建模器应用程序绘制流程图


### Docker搭建maven私服
0. 准备
mkdir -p /usr/local/work/maven
chmod 777 /usr/local/work/maven

1. 运行
docker run -d -p 8081:8081 --name nexus3 -v /usr/local/work/maven:/var/nexus-data -e INSTALL4J_ADD_VM_PARAMS="-Xms256M -Xmx512M -XX:MaxDirectMemorySize=512M" sonatype/nexus3

2. 点击右上角 sign in 根据提示修改密码
http://192.168.184.6:8081/

3. 设置仓库Deployment Policy配置为Allow Redeploy
repository/repositories:maven-releases>Hosted>选择Allow redeploy

4. 配置maven配置文件settings.xml
<server> 
  <!-- id自定义和pom.xml配置的id一致 -->
  <id>maven-releases</id>
  <username>admin</username>
  <password>123456</password>
</server>
<server>
  <id>maven-snapshots</id>
  <username>admin</username>
  <password>123456</password>
</server>

5. 配置项目的pom.xml文件
<distributionManagement>
    <repository>
        <!-- 这里的id要和setting里配置的id对应-->
        <id>maven-releases</id>
        <url>http://192.168.184.6:8081/repository/maven-releases/</url>
    </repository>
    <snapshotRepository>
        <id>maven-snapshots</id>
        <url>http://192.168.184.6:8081/repository/maven-snapshots/</url>
    </snapshotRepository>
</distributionManagement>

6. deploy命令推送类库到远程 

7. maven配置阿里云镜像加速,setting配置找到标签,添加如下:
<mirrors>
    <mirror>
        <id>alimaven</id>
        <name>aliyun maven</name>
        <url>http://maven.aliyun.com/nexus/content/repositories/central/</url>
        <mirrorOf>central</mirrorOf>
    </mirror>
    <mirror>
        <id>alimaven</id>
        <name>aliyun maven</name>
        <url>http://maven.aliyun.com/nexus/content/groups/public/</url>
        <mirrorOf>central</mirrorOf>
    </mirror>
</mirrors>

8. 缓存开源三方依赖到私服
在setting文件中找到mirrors标签中添加Nexus库,id和server标签的id对应
<mirror>
    <id>maven-releases</id>
    <name>Nexus</name>
    <mirrorOf>central</mirrorOf>
    <url>http://192.168.67.6:8081/repository/maven-public/</url>
</mirror>

然后在maven项目中执行 mvn clean compile 命令，下载依赖后，Nexus服务器就有jar包

9. 引用私服中的jar, pom.xml中添加
<repositories>
    <repository>
        <id>maven-releases</id>
        <url>http://192.168.67.6:8081/repository/maven-public/</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
        <releases>
            <enabled>true</enabled>
        </releases>
    </repository>
</repositories>


## 搭建zk集群
关闭防火墙, 重启后会打开
systemctl stop firewalld.service
systemctl status firewalld.service

永久关闭 禁用防火墙服务
systemctl disable firewalld

创建目录
mkdir -p /home/zookeeper_data/{data,conf}
sudo chmod -R 777 /home/zookeeper_data
搭建zookeeper集群3台,另外两台一样zk02, zk03, 仅修改 ZOO_MY_ID=1

docker run -d --name zk01 --privileged=true --network host \
-e ZOO_MY_ID=1 \
-e "ZOO_SERVERS=server.1=192.168.38.128:2888:3888 server.2=192.168.38.130:2888:3888 server.3=192.168.38.132:2888:3888" \
-p 2181:2181 -p 2888:2888 -p 3888:3888 \
-v /home/zookeeper_data/data:/data \
docker.io/zookeeper:3.4.12

修改配置添加集群ip, data中的myid文件为 "1" 对应 server.1 (可以ZOO_SERVERS方式参数替代,不用配置)
vim /home/zookeeper_data/conf/zoo.cfg
server.1=192.168.67.6:2888:3888
server.2=192.168.67.128:2888:3888
server.3=192.168.67.3:2888:3888

必须分别启动3个节点后, 再查看集群状态 leader 表示主节点
docker exec -it 44488e07dd80 /bin/bash
./bin/zkServer.sh status
3台分别显示
Mode: follower
Mode: leader
Mode: follower

### centos基础镜像
1. 编写Dockerfile文件
vim Dockerfile
FROM centos:centos7.9.2009
MAINTAINER lhm
ENV MYPATH /home
WORKDIR $MYPATH
RUN yum -y install vim
RUN yum install -y telnet
EXPOSE 80
CMD ["/bin/bash"]

2. 打包镜像
docker image build ./ -t centos7:0.0.1

3. 运行
docker container run -p 8011:80 centos7:0.0.1

4. 推送到仓库

### xxl-job
1. github下载源码: https://github.com/xuxueli/xxl-job
2. 创建数据库表格, 修改 xxl-job-admin 项目数据库配置, 启动项目. 调度中心访问地址：http://localhost:8080/xxl-job-admin
3. 执行器项目 pom.xml 添加依赖 xxl-job-core
4. 在执行器项目添加java配置类, 再新建一个demo job
5. 登录调度中心,添加执行器, 再添加任务(运行模式选择 BEAN)


### 搭建gitlib
docker pull gitlab/gitlab-ce:15.1.6-ce.0
mkdir -p /mnt/gitlab/{etc,log,data}
sudo chmod -R 777 /mnt/gitlab

docker run -d -p 8443:443 -p 8090:80 -p 8022:22 --name gitlab15 \
-v /mnt/gitlab/etc:/etc/gitlab \
-v /mnt/gitlab/logs:/var/log/gitlab \
-v /mnt/gitlab/data:/var/opt/gitlab \
--privileged=true \
gitlab/gitlab-ce:15.1.6-ce.0

进入容器查看密码
vi /etc/gitlab/initial_root_password
登录
http://192.168.67.6:8090/
root/密码


### Ansibles使用
yum install epel-release -y
yum install ansible –y

安装目录如下
配置文件目录：/etc/ansible/
执行文件目录：/usr/bin/
Lib库依赖目录：/usr/lib/pythonX.X/site-packages/ansible/
Help文档目录：/usr/share/doc/ansible-X.X.X/
Man文档目录：/usr/share/man/man1/

ansible默认host文件, 添加一组服务器, 指定用户密码
vi /etc/ansible/hosts
[testname]
192.168.67.6 ansible_user=root ansible_ssh_pass=LHM@1111

验证某一组清单
-i 指定hosts文件路径，可以不使用/etc/ansible/hosts
-m 指定使用shell模块
-a "" 为参数设置

发请求
ansible testname -m shell -a "curl http://127.0.0.1:8080/"

查日志
ansible kfpt4 -m shell -a "grep 仅生产生产环境开启同步 /opt/localcode_apply/logs/project_back_ssm_localcode_apply.log"

统计文本中关键字数量
grep '关键字' project_back_ssm_verification.log | wc -l

### chrom忽略https证书校验
chrom属性>目标中添加参数
D:\work\tool\Chrome_v87\Chrome\Application\chrome.exe --test-type --ignore-certificate-errors

# ES
docker run -d --name es74 -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" elasticsearch:7.4.2