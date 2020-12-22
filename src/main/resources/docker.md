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
docker build  根据Dockerfile打包镜像
docker pull   拉取远程仓库镜像
docker run    运行容器
docker stop xxx  停止容器 xxx为运行容器ID

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









