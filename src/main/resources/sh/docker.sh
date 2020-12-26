image_name=helloboot
container_id = $(docker ps |grep "$image_name" |awk '{print $1}')
image_id=$(docker images |grep "$image_name" |grep 0.0.1 | awk'{print $3}')

cd /var/jenkins_mount/workspace/helloboot
docker stop $container_id
docker rmi $image_id

docker image build ./ -t $image_name:0.0.1
docker container run -p 8086:8086 $image_name:0.0.1

echo "$image_name 容器创建完成"


