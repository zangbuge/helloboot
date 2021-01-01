#!/bin/bash
BUILD_ID=dontkillme

image_name=helloboot  # 注意 反引号
container_id=`docker ps |grep "$image_name" |awk '{print $1}'`
image_id=`docker images |grep "$image_name" |grep 0.0.1 |awk '{print $3}'`
echo "$container_id 容器ID"
echo "$image_id 镜像ID"

cd /var/lib/jenkins/workspace/helloboot
docker stop $container_id
docker rmi $image_id

docker image build ./ -t $image_name:0.0.1
docker container run -d --rm --name $image_name -p 8086:8086 -v /app/logs:/app/logs $image_name:0.0.1

echo "$image_name 容器启动完成"


