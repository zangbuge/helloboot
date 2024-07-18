### 一 sql
-- mysql 5.7+ 设置默认更新时间
create table test(
id integer not null auto_increment primary key,
name varchar(20) not null ,
create_time timestamp not null default CURRENT_TIMESTAMP COMMENT '创建时间',
update_time timestamp not null default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP COMMENT '更新时间');

-- mysql 多字段使用 in 条件
select * from test s where (s.from_type , s.params) in (('zfb', '00'), ('smy', '333'));

-- mysql where 条件避免verchar=数字; 如 where status = 0; 字母也会被匹配出来
当比较一个 VARCHAR 类型的字段和一个整数时，MySQL 会将该字段尝试转换为整数，然后再与整数进行比较。
而在这个转换过程中，如果字段的值无法转换为整数，则会被转换为 0 

### 二 mysql死锁
2.1 查看所有正在运行线程
SHOW FULL PROCESSLIST;

Id：线程的唯一标识符
User：连接数据库的用户名
Host：连接数据库的主机名
db：当前连接的数据库
Command：线程正在执行的命令类型
Time：线程已经执行的时间
State：线程的当前状态
Info：线程正在执行的sql语句

观察State列,我们可以找出正在等待锁资源或者正在锁定其他事务的线程。
Waiting for table metadata lock 表示线程正在等待表的元数据锁
Waiting for table level lock 表示线程正在等待表级别的锁
Waiting for lock 表示线程正在等待其他锁
Waiting for table flush 线程正在执行FLUSH TABLES并等待所有线程关闭它们的表，
或者线程收到通知，表明表的基础结构已更改，需要重新打开表以获取新结构。但是，要重新打开该表，它必须等到所有其他线程都关闭了该表

观察Command列
Sleep 线程正在等待客户端向它发送一条新语句,空闲状态
Query 线程正在执行查询语句
Kill 该线程正在杀死另一个线程

总条数即为客户端创建的连接数,也可以执行以下命令查看
show status like 'Threads%';

2.2 查看正在进行中的事务
SELECT * FROM information_schema.INNODB_TRX;
trx_state   事务状态, 包括 RUNNING（运行中）、LOCK WAIT（等待锁）、ROLLING BACK（回滚中）、COMMITTING（提交中）等
trx_started 事务等待开始时间
trx_query   事务执行的SQL语句

2.3 查看死锁日志,要查看被阻塞的事务,包括当前的锁表情况
SHOW ENGINE INNODB STATUS;
出现以下3个步骤,开启事物,锁住,等待的死循环
*** (1) TRANSACTION: (这里可以看到相关sql)
*** (1) HOLDS THE LOCK(S):
*** (1) WAITING FOR THIS LOCK TO BE GRANTED:
在TRANSACTIONS部分，可以找到当前执行的事务列表。显示每个事务的ID、等待的锁资源、事务的状态以及每个事务正在执行的SQL语句
在LATEST DETECTED DEADLOCK部分，可以找到最近被检测到的死锁信息

2.4 手动模式堵塞
select count(1), sleep(3000) from test_user;

2.5 用于查看当前打开的表，以及锁定的表
SHOW OPEN TABLES WHERE In_use > 0;
In_use 表示有多少线程在使用这张表, 结果中包含In_use的值大于0
Name_locked 表名是否被锁定，0代表正常

2.6 杀死造成死锁的进程
KILL <thread_id>;

2.7 查看mysql连接数
show status like 'Threads%'; -- Threads_connected值等于 SHOW PROCESSLIST; 的总数. 
SHOW PROCESSLIST;
-- 如果发现Threads_created值过大的话，表明MySQL服务器一直在创建线程, 可以适当增加配置文件中的thread_cache_size值
SHOW VARIABLES LIKE 'max_connections'; -- 最大连接数 默认151

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
./mysqld --initialize --console --lower-case-table-names=1   -- 生产临时密码
./mysqld --install mysql8  -- 安装
net start mysql8  -- 启动
./mysql -uroot -p -P3308 -- 登录 指定端口登录的P大写
修改密码8.0以后:
alter user 'root'@'localhost' identified by '123456' password expire never;
alter user 'root'@'localhost' identified with mysql_native_password by '123456';
flush privileges;

查看设置用户授权访问ip, %为任何ip都可访问, 重启服务生效
select user, host from mysql.user;


mysql8 my.ini 配置文件在根目录下 
```aidl
[mysql]
default-character-set=utf8
[mysqld]
port=3308
basedir=D:/work/tool/database/mysql8data
datadir=D:/work/tool/database/mysql8data/data
max_connections=500
character-set-server=utf8
default-time_zone='+8:00'
default-storage-engine=INNODB
bind-address=0.0.0.0
sql-mode="STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION"
secure_file_priv=
```

