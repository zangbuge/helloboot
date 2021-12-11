参考资料
http://c.biancheng.net/view/3118.html
https://zhuanlan.zhihu.com/p/83309276

环境
腾讯云 centos v7.6

条件
要求linux内核版本高于3.10
uname -r  #查看centos内核版本

yum方式安装
yum install -y yum-utils device-mapper-persistent-data lvm2   #安装docker所需的依赖软件包
yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo  #使用阿里云镜像
yum makecache fast			#生成缓存将软件包信息提前在本地缓存一份，用来提高搜索安装软件的速度
rpm --import https://mirrors.aliyun.com/docker-ce/linux/centos/gpg   #增加docker资源库
yum -y install docker-ce 使用yum安装社区版本
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
docker search 在Docker Hub中查找镜像 
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
#启动mysql
docker run -d --name mysql57 -p 3306:3306 -e TZ="Asia/Shanghai" -v $PWD/mysql/conf:/etc/mysql/conf.d -v $PWD/mysql/db:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=123456 mysql:5.7 --lower_case_table_names=1

MYSQL_ROOT_PASSWORD=123456：设置 MySQL 服务 root 用户的密码 即可登录mysql
lower_case_table_names=1  #表示数据库不区分大小写, 否则表名不一致会报错

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

docker logs -f -t --tail 100 datacenterId  #查看容器启动项目的日志
-f 跟踪实时日志 --follow
-t 显示时间戳
100 只显示最后100行

docker logs --since 30m CONTAINER_ID #查看最近30分钟的日志

# 进入到应用容器内 容器尚未bash安装  使用bash进入会出现错误: starting container process caused: exec: "bash": executable file not found in $PATH: unknown
docker exec -it helloboot sh


#### 安装mongo
docker run --name mymongo -d -p 27017:27017 -v /data/configdb:/data/configdb -v /data/mongo:/data/db mongo
docker exec -it mymongo mongo admin  #进入mongo命令
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
设置jenkins和系统时间一致, 新增配置
JAVA_ARGS="-Dorg.apache.commons.jelly.tags.fmt.timeZone=Asia/Shanghai -Dfile.encoding=UTF-8 -Djava.awt.headless=true"
#将Jenkins用户添加到根组, 解决安装插件时"无法连接到Jenkins"问题
sudo usermod -a -G root jenkins
#启动服务
service jenkins start
#查看admin密码
vi /var/lib/jenkins/secrets/initialAdminPassword

#安装git  然后jenkins中git配置选择自动安装
sudo yum install -y git

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
docker run -itd --name nacos --restart=always --network common-network --env MODE=standalone -p 8848:8848 nacos/nacos-server 
#管理界面 默认端口号是：8848
http://127.0.0.1:8848/nacos/
#默认账号密码：nacos/nacos

### 指定mysql配置  MODE 默认cluster 集群模式
```aidl
docker run -d \
-e MODE=standalone \
-e SPRING_DATASOURCE_PLATFORM=mysql \
-e MYSQL_SERVICE_HOST=115.159.66.110 \
-e MYSQL_SERVICE_PORT=3306 \
-e MYSQL_SERVICE_USER=root \
-e MYSQL_SERVICE_PASSWORD=123456 \
-e MYSQL_SERVICE_DB_NAME=nacos_config \
-p 8848:8848 \
--restart=always \
--name mynacos \
-v /root/nacos/standalone-logs/:/home/nacos/logs \
nacos/nacos-server:1.2.1
```

### 安装redis
#快捷启动
docker run -itd --name redis-test -p 6379:6379 redis
#制定配置启动
#宿主机下添加配置文件 /data/redis/conf/redis.conf
docker run -itd -p 6379:6379 --rm --name myredis -v /data/redis/conf:/etc/redis/conf -v /data/redis/db:/data redis redis-server /etc/redis/conf/redis.conf

#### k8s
安装epel-release源
yum -y install epel-release
关闭防火墙
systemctl stop firewalld
systemctl disable firewalld
setenforce 0
查看防火墙状态
firewall-cmd --state
关闭swap
swapoff -a

安装Kubeadm, 配置yum源 执行如下命令：
cat <<EOF > /etc/yum.repos.d/kubernetes.repo 
[kubernetes] 
name=Kubernetes 
baseurl=https://mirrors.aliyun.com/kubernetes/yum/repos/kubernetes-el7-x86_64 
enabled=1 
gpgcheck=0 
repo_gpgcheck=0
gpgkey=https://mirrors.aliyun.com/kubernetes/yum/doc/yum-key.gpg https://mirrors.aliyun.com/kubernetes/yum/doc/rpm-package-key.gpg
EOF

#查看支持k8s版本
yum list kubelet --showduplicates | sort -r 
#安装docker
yum install docker-ce-18.09.6 docker-ce-cli-18.09.6
# 首先查看已安装Docker
yum list installed |grep docker
# 执行卸载
yum -y remove docker-ce.x86_64

#安装etcd
yum install etcd -y
#启动etcd
systemctl start etcd
systemctl enable etcd
#输入如下命令查看 etcd 健康状况
etcdctl -C http://localhost:2379 cluster-health

#### 安装 Kubernetes 
如果出现冲突,使用命令卸载
yum remove  kubernetes-client-1.5.2-0.7.git269f928.el7.x86_64

##### k8s会自动安装docker, 必须卸载已安装的docker 否则冲突
yum install kubernetes -y
cd /etc/kubernetes
ls #成功会有以下文件
apiserver  apiserver.rpmnew  apiserver.rpmsave  config  controller-manager  manifests  scheduler

#查看k8s版本
kubectl version
docker --version
etcd --version 
cat /etc/redhat-release
#### 启动etcd、kube-apiserver、kube-controller-manager、kube-scheduler等服务，并设置开机启动。
for SERVICES in etcd kube-apiserver kube-controller-manager kube-scheduler; do systemctl restart $SERVICES;systemctl enable $SERVICES;systemctl status $SERVICES ; done

#安装 flannel 网络管理组件
yum install flannel -y
#在etcd中定义flannel网络
etcdctl mk /atomic.io/network/config '{"Network":"172.17.0.0/16"}'

#安装node组件
yum -y install flannel kubernetes-node

#启动修改后的 flannel ，并依次重启 docker、kubernete
service docker restart
systemctl restart kube-apiserver
systemctl restart kube-controller-manager
systemctl restart kube-scheduler
systemctl enable flanneld
systemctl start flanneld
systemctl restart kubelet
systemctl restart kube-proxy

#node节点机上启动kube-proxy,kubelet,docker,flanneld等服务，并设置开机启动
for SERVICES in kube-proxy kubelet docker flanneld;do systemctl restart $SERVICES;systemctl enable $SERVICES;systemctl status $SERVICES; done

#查看k8s集群状态
kubectl get no 
#查看运行的node节点机器
kubectl get nodes

-------------------------------------------------------
Warning: RPMDB altered outside of yum
【解决办法】删除 yum 的历史记录
rm -rf /var/lib/yum/history/*.sqlite 

Kubernetes集群组件
etcd 一个高可用的K/V键值对存储和服务发现系统
flannel 实现夸主机的容器网络的通信
kube-apiserver 提供kubernetes集群的API调用
kube-controller-manager 确保集群服务
kube-scheduler 调度容器，分配到Node
kubelet 在Node节点上按照配置文件中定义的容器规格启动容器
kube-proxy 提供网络代理服务

kubeadm  启动 k8s 集群的命令工具
kubelet  集群容器内的命令工具
kubectl  操作集群的命令工具
yum install docker-ce kubeadm kubelet kubectl -y

启动
systemctl enable etcd
systemctl enable kube-apiserver
systemctl enable kube-controller-manager
systemctl enable kube-scheduler

systemctl start etcd
systemctl start kube-apiserver
systemctl start kube-controller-manager
systemctl start kube-scheduler
