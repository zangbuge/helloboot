FROM maven:3.5.2-jdk-8-alpine AS MAVEN_BUILD
MAINTAINER lhm
COPY pom.xml /build/
COPY src /build/src/
COPY settings.xml /usr/share/maven/conf/settings.xml
WORKDIR /build/
RUN mvn dependency:go-offline -B
RUN mvn -o -s /usr/share/maven/conf/settings.xml clean package -Ptest
FROM openjdk:8-jre-alpine
WORKDIR /app
COPY --from=MAVEN_BUILD /build/target/helloboot-0.0.1-SNAPSHOT.jar /app/
ENTRYPOINT ["nohup", "java", "-jar", "helloboot-0.0.1-SNAPSHOT.jar", " &"]


### 说明
# nohup java -Dspring.profiles.active=local -jar helloboot-0.0.1-SNAPSHOT.jar >/dev/null 2>&1 &
# SpringBoot启动参数【-D】和【--】的区别:【-D】虚拟机参数要放到 -jar 前面,否则无效.【--】参数从main方法参数传入，springboot会对这种参数进行自动解析,参数不能放到前面，否则会报错
# FROM maven:3.5.2-jdk-8-alpine AS MAVEN_BUILD 告知Docker采用Maven编译器
# MAINTAINER lhm 非必选项，但是为映像作者提供一个接触点可提高可维护性
# COPY pom.xml/build/ 在镜像中创建一个build目录， 并拷入pom.xml文件
# WORKDIR/build/  设置build为工作目录,后续任何命令都在此目录中运行
# RUN mvn package 执行mvn包来运行编译和打包应用, 在第一次构建镜像时从公共Maven库拉取依赖，并将它们缓存在镜像的本地
# FROM openjdk:8-jre-alpine 告知Docker多阶段构建的下一步采用openjdk:8-jre-alpine的基础镜像
# COPY--from=MAVEN_BUILD/build/target/docker-boot-intro-0.1.0.jar  /app/ 告知Docker从MAVEN_BUILD阶段的/build/target目录复制ocker-boot-intro-0.1.0.jar到/app目录
# ENTRYPOINT["java","-jar","app.jar"] 告知Docker在容器运行本镜像时，运行哪些命令
# COPY settings.xml /usr/share/maven/conf/settings.xml 使用自定义配置maven仓库位置
# RUN mvn dependency:go-offline 结合docker的cache机制,避免maven的依赖每次都要重新下载,且在pom.xml配置缓存插件

# JVM参数设置
# -Xms1024m (堆初始大小)
# -Xmx1024m (堆最大大小) 此值可以设置与-Xmx相同，以避免每次垃圾回收完成后JVM重新分配内存
# -Xmn256m (新生代大小)  Sun官方推荐配置为整个堆的3/8, 整个JVM内存大小 = 年轻代大小 + 年老代大小 + 持久代大小。持久代一般固定大小为64m，所以增大年轻代后，将会减小年老代大小。此值对系统性能影响较大
# -Xss256k (棧最大深度大小) 设置每个线程的堆栈大小
