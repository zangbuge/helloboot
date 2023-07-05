-- mysql 5.7+ 设置默认更新时间
create table test(
id integer not null auto_increment primary key,
name varchar(20) not null ,
create_time timestamp not null default CURRENT_TIMESTAMP COMMENT '创建时间',
update_time timestamp not null default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP COMMENT '更新时间');


mysql绿色版安装
官方下载地址: https://downloads.mysql.com/archives/community/
安装包下载完之后，
1. bin目录管理员权限执行 mysqld --install 命令安装服务
2. 执行 net start mysql 命令启动服务
3. 登录mysql：mysql -uroot -p
4. 修改密码:　
8.0以前: set password for root@localhost = password('123456');


指定服务名称安装mysql8 管理员权限运行, 设置忽略大小写必须在初始化时设置
输入命令：sc delete mysql 删除该mysql
mysqld --initialize --console --lower-case-table-names=1   -- 生产临时密码
mysqld --install mysql8
net start mysql8
登录: mysql -uroot -p -P3308 -- 指定端口登录的P大写
修改密码8.0以后:
alter user 'root'@'localhost' identified by '123456' password expire never;
alter user 'root'@'localhost' identified with mysql_native_password by '123456' ;
flush privileges;


mysql8 my.ini 配置文件 
[mysql]
# 设置mysql客户端默认字符集
default-character-set=utf8
[mysqld]
port=3308
basedir=D:\mysql-8\
datadir=D:\mysql-8\data
max_connections=200 
character-set-server=utf8
default-time_zone='+8:00'
default-storage-engine=INNODB
bind-address=0.0.0.0
sql-mode="STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION"
secure_file_priv=
