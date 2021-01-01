FROM maven:3.5.2-jdk-8-alpine AS MAVEN_BUILD
MAINTAINER Brian Hannaway
COPY pom.xml /build/
COPY src /build/src/
WORKDIR /build/
RUN mvn package:go-offline
FROM openjdk:8-jre-alpine
WORKDIR /app
COPY --from=MAVEN_BUILD /build/target/helloboot-0.0.1-SNAPSHOT.jar /app/
ENTRYPOINT ["nohup ", "java", "-jar", "helloboot-0.0.1-SNAPSHOT.jar", "> /var/log/start.log", " &"]


### 说明
# FROM maven:3.5.2-jdk-8-alpine AS MAVEN_BUILD 告知Docker采用Maven编译器
# MAINTAINERBrianHannaway非必选项，但是为映像作者提供一个接触点可提高可维护性
# COPY pom.xml/build/ 在镜像中创建一个build目录， 并拷入pom.xml文件
# WORKDIR/build/  设置build为工作目录,后续任何命令都在此目录中运行
# RUN mvn package 执行mvn包来运行编译和打包应用, 在第一次构建镜像时从公共Maven库拉取依赖，并将它们缓存在镜像的本地
# FROM openjdk:8-jre-alpine 告知Docker多阶段构建的下一步采用openjdk:8-jre-alpine的基础镜像
# COPY--from=MAVEN_BUILD/build/target/docker-boot-intro-0.1.0.jar  /app/ 告知Docker从MAVEN_BUILD阶段的/build/target目录复制ocker-boot-intro-0.1.0.jar到/app目录
# ENTRYPOINT["java","-jar","app.jar"] 告知Docker在容器运行本镜像时，运行哪些命令