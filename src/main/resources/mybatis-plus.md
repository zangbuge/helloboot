mybatisplus官网文档
https://baomidou.com/pages/a61e1b/#dynamic-datasource

1. 引入依赖
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>dynamic-datasource-spring-boot-starter</artifactId>
    <version>3.6.1</version>
</dependency>

2. 配置yml
spring:
  datasource:
    dynamic:
      primary: master #设置默认的数据源或者数据源组,默认值即为master
      strict: false #严格匹配数据源,默认false. true未匹配到指定数据源时抛异常,false使用默认数据源
      datasource:
        master:
          url: jdbc:mysql://xx.xx.xx.xx:3306/dynamic
          username: root
          password: 123456
          driver-class-name: com.mysql.jdbc.Driver # 3.2.0开始支持SPI可省略此配置
        slave_1:
          url: jdbc:mysql://xx.xx.xx.xx:3307/dynamic
          username: root
          password: 123456
          driver-class-name: com.mysql.jdbc.Driver
        slave_2:
          url: ENC(xxxxx) # 内置加密,使用请查看详细文档
          username: ENC(xxxxx)
          password: ENC(xxxxx)
          driver-class-name: com.mysql.jdbc.Driver
       #......省略
       #以上会配置一个默认库master，一个组slave下有两个子库slave_1,slave_2


3 使用 @DS 切换数据源

4 开启事务
@DSTransactional


5 count()函数
count(*)：统计记录总数，包含重复的记录，以及为NULL或空的记录。
count(1)：根据第一列统计记录总数，包含重复的记录，包含为NULL或空的值。也可以使用count(2)、count(3)等等。
count(列名)：根据指定的列统计记录总数，包含重复的记录，不包括NULL或空的值。
count(distinct 列名)：根据指定的列统计记录总数，不包含重复的记录，不包括NULL或空的值。
