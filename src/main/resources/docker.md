参考资料
http://c.biancheng.net/view/3118.html
https://zhuanlan.zhihu.com/p/83309276

环境
腾讯云 centos v7.6

条件
要求linux内核版本高于3.10
uname -r  #查看centos内核版本
yum -y update 更新yum包到最新版本

yum方式安装
yum install -y yum-utils device-mapper-persistent-data lvm2   #安装docker所需的依赖软件包
yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo  #使用阿里云镜像
yum makecache fast			#生成缓存将软件包信息提前在本地缓存一份，用来提高搜索安装软件的速度
rpm --import https://mirrors.aliyun.com/docker-ce/linux/centos/gpg   #增加docker资源库
yum -y install docker-ce 使用yum安装社区版本
systemctl enable docker  让docker加入开机启动
systemctl restart docker 启动docker

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
docker search 在Docker Hub中查找镜像 
-d: 后台运行容器，并返回容器ID； 
-name=”nginx-lb”: 为容器指定一个名称； 

docker架构3个部分
client      客户端执行命令
dockerhost  包含containers, images
registry    注册中心, 镜像仓库,类似maven仓库

快速启动容器 -p 表示映射主机8880端口到docker的80端口
docker run --rm --name myNginx -p 8880:80 nginx  #本地没有镜像则去远程仓库下载该镜像
很可能无法正常拉取镜像，所以就需要我们为docker设置国内阿里云的镜像加速器
需要修改配置文件后,docker必须重启 vi /etc/docker/daemon.json
{ 
"registry-mirrors": ["https://alzgoonw.mirror.aliyuncs.com"] 
}

启动后可以通过主机8880端口访问nginx了
启动一个容器, 即创建一个linux系统,里运行着nginx

进入容器
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



#### docker安装mysql
docker pull mysql:5.7  #拉取docker mysql官方镜像 或 docker pull mysql:latest  最新的
docker images          
docker run --name mysql5.7 -p 3306:3306 -e MYSQL_ROOT_PASSWORD=123456 -d mysql:5.7  #启动mysql
MYSQL_ROOT_PASSWORD=123456：设置 MySQL 服务 root 用户的密码
即可登录mysql

mysql的默认配置文件是 /etc/mysql/my.cnf 文件, 如果想要自定义配置，建议向 /etc/mysql/conf.d 目录中创建 .cnf 文件
进入容器
docker exec -it mysql5.7 bash
登录mysql
mysql -u root -p
ALTER USER 'root'@'localhost' IDENTIFIED BY 'Lzslov123!';
添加远程登录用户
CREATE USER 'liaozesong'@'%' IDENTIFIED WITH mysql_native_password BY '123456';
GRANT ALL PRIVILEGES ON *.* TO 'liaozesong'@'%';


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


#### 安装mongo
docker run --name mymongo -d -p 27017:27017 mongo
docker exec -it xxx mongo admin  #进入mongo命令
db.createUser({ user:'admin',pwd:'123456',roles:[ { role:'root', db: 'admin'}]})  #创建用户

#### 推送镜像到远程仓库
docker login -u 用户名  #登录到官方Docker Hub仓库
必须先打一个tag再推送
docker tag helloboot:0.0.1 zangbuge/helloboot
docker push zangbuge/helloboot

#### 安装jenkins
# -v /var/jenkins_mount:/var/jenkins_mount 挂载目录
# -v /etc/localtime:/etc/localtime让容器使用和服务器同样的时间设置
mkdir -p /var/jenkins_mount  #先创建一个jenkins工作目录 
sudo chmod -R 777 /var/jenkins_mount  #设置权限 -R 指级联应用到目录里的所有子目录和文件, 777 是所有用户都拥有最高权限
docker run -u 1000:1000 --name myjenkins -d -p 18080:8080 -p 50000:50000 -v /var/jenkins_mount:/var/jenkins_home --privileged jenkins/jenkins
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


#### centos安装jenkins
#先安装jdk, jenkins是基于java环境的
yum -y install java-1.8.0-openjdk
#下载源文件 国内清华大学镜像(官网极慢)
wget https://mirrors.tuna.tsinghua.edu.cn/jenkins/redhat/jenkins-2.271-1.1.noarch.rpm
#安装
rpm -ivh jenkins-2.271-1.1.noarch.rpm
#修改端口 JENKINS_PORT="8080"
vi /etc/sysconfig/jenkins 
#启动服务
service jenkins start

登陆jenkins后创建项目后只需在 源码管理 中配置项目git地址
然后在构建中添加shell脚本
```aidl
#!/bin/bash
sudo /var/lib/jenkins/workspace/helloboot/src/main/resources/sh/docker.sh
```










