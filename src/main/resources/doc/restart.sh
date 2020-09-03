#!/bin/bash
pid=`ps -ef|grep helloboot |grep java|awk '{print $2}'`
echo "old pid: $pid"
if [ -n "$pid" ]
then
kill -9 $pid
fi
mvn clean package
java -jar ./target/helloboot-0.0.1-SNAPSHOT.jar &

# 必须设置脚本格式为unix
# vi deploy.sh   Windows中默认格式是dos. 查看命令  :set ff   修改为 :set ff=unix
# jenkins 中配置 Execute shell命令:

#当jenkins进程结束后新开的tomcat进程不被杀死
BUILD_ID=DONTKILLME

#加载环境变量
. /etc/profile

#sh /var/lib/jenkins/workspace/helloboot/restart.sh
sh /var/lib/jenkins/workspace/deploy.sh