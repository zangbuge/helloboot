-- mysql 5.7+ 设置默认更新时间
create table test(
id integer not null auto_increment primary key,
name varchar(20) not null ,
create_time timestamp not null default CURRENT_TIMESTAMP COMMENT '创建时间',
update_time timestamp not null default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP COMMENT '更新时间');

-- mysql 多字段使用 in 条件
select * from test s where (s.from_type , s.params) in (('zfb', '00'), ('smy', '333'));

-- 查看当前的锁表情况
SHOW FULL PROCESSLIST;
Id：线程的唯一标识符
User：连接数据库的用户名
Host：连接数据库的主机名
db：当前连接的数据库
Command：线程正在执行的命令
Time：线程已经执行的时间
State：线程的当前状态
Info：线程正在执行的查询语句
通过观察State列，我们可以找出正在等待锁资源或者正在锁定其他事务的线程。其中，Waiting for table metadata lock表示线程正在等待表的元数据锁，Waiting for table level lock表示线程正在等待表级别的锁，Waiting for lock表示线程正在等待其他锁。

-- 杀死造成死锁的进程
KILL <thread_id>;

-- 要查看被阻塞的事务,包括当前的锁表情况
SHOW ENGINE INNODB STATUS;
在TRANSACTIONS部分，可以找到当前执行的事务列表。显示每个事务的ID、等待的锁资源、事务的状态以及每个事务正在执行的SQL语句
在LATEST DETECTED DEADLOCK部分，可以找到最近被检测到的死锁信息

```$xslt
-- 添加字段不会锁定整个表，但修改或删除现有字段会对整个表加锁。显式地对表加读锁或写锁，以控制其他事务对表的访问
-- 锁定表
LOCK TABLES table_name WRITE;

-- 执行需要操作该表的SQL语句
...

-- 提交或者回滚事务
COMMIT; -- 或 ROLLBACK;

-- 解除表锁
UNLOCK TABLES;
```

-- 用于查看当前打开的表，以及锁定的表
SHOW OPEN TABLES WHERE In_use > 0;
结果中包含In_use的值大于0，这表示相应的表被锁住了,可以使用 UNLOCK TABLES; 释放锁

mysql绿色版安装
官方下载地址: https://downloads.mysql.com/archives/community/
安装包下载完之后，
bin目录"管理员权限执行"
0. 初始化临时密码打印在最后一行 ./mysqld --initialize --console
1. 安装 ./mysqld --install mysql57 --defaults-file="..\my.ini" 命令安装服务
2. 执行 net start mysql57 命令启动服务
3. 登录mysql: ./mysql -P3307 -uroot -p
4. 修改密码:　
8.0以前: set password for root@localhost = password('123456');
设置允许远程访问:
mysql -u root -p123456
GRANT ALL PRIVILEGES ON *.* TO root@"%" IDENTIFIED BY "root";
flush privileges;
mysql -h 127.0.0.1 -u root -p123456;   --##测试远程登录

或设置管理员账号 mysqladmin -u root password '123456'
更改后，一定注意执行flush privileges;


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

查看设置用户授权访问ip, %为任何ip都可访问
select user, host from mysql.user;

重启服务生效

mysql8 my.ini 配置文件在根目录下 
[mysql]
default-character-set=utf8
[mysqld]
port=3308
basedir=D:\mysql-8\
datadir=D:\mysql-8\data
max_connections=500 
character-set-server=utf8
default-time_zone='+8:00'
default-storage-engine=INNODB
bind-address=0.0.0.0
sql-mode="STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION"
secure_file_priv=
