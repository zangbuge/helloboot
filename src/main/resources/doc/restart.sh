#当jenkins进程结束后新开的tomcat进程不被杀死
BUILD_ID=DONTKILLME

#加载环境变量
. /etc/profile

#查询线程ID
pid=`ps -ef|grep helloboot|grep java|awk '{print $2}'`

#关闭应用
kill -9 $pid

mvn clean package

java -jar ./target/helloboot-0.0.1-SNAPSHOT.jar &
