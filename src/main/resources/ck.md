部署ck 3分片3副本, 共6个ck服务, 3个zk节点
节点 vim /etc/hosts 
192.168.38.128 node1
192.168.38.130 node2
192.168.38.132 node3

ClickHouse官方文档: https://clickhouse.com/docs/zh/getting-started/install
### 运行ClickHouse 使用dbevear登录,默认用户名密码: default/空
运行临时容器
docker run -d -p 8123:8123 -p 9000:9000 --name clickhouse yandex/clickhouse-server:21.3.20

创建目录
mkdir -p /home/clickhouse/main/{conf,data,log}
chmod 777 /home/clickhouse

拷贝配置文件
docker cp 0b88ed38fa4f:/etc/clickhouse-server/users.xml /home/clickhouse/main/conf/users.xml
docker cp 0b88ed38fa4f:/etc/clickhouse-server/config.xml /home/clickhouse/main/conf/config.xml

vi users.xml  设置密码
<users>
   <!-- 添加账号,账号名: ck_user, 密码 root,.123 -->
   <ck_user> 
       <!--密码-->
       <!--
       <password>root,.123</password>
       -->
       <!--加密密码-->
       <password_sha256_hex>a14c4c9d228e0cc32814050fea0f1df49dad0e1857615f5c7900bcb8d33b55a1</password_sha256_hex>
       <!--用户可以从中连接到ClickHouse服务器的网络列表-->
       <networks>
           <ip>::/0</ip>
       </networks>
       <!--可以配读写,只读,写等 不一一列举,自行查阅-->
       <profile>default</profile>
       <!--限制用户使用资源,自行查阅-->
       <quota>default</quota>
       <!--(超级权限)用户可以创建其他用户，并赋予其他用户权限 ,0关闭,1开启-->
       <access_management>1</access_management>
   </ck_user>
</users> 

vi config.xml 修改监听host
<listen_host>0.0.0.0</listen_host>

单机配置结束, 集群配置文件需要再修改

config.xml 添加如下信息,删除原始的 remote_servers, zookeeper 标签
<!-- 外部依赖配置文件, 这里的路径是相对容器内的不要改 -->
<include_from>/etc/clickhouse-server/metrika.xml</include_from>
<!-- 集群相关的配置，可以用外部依赖文件来配置 -->
<remote_servers incl="clickhouse_remote_servers" optional="true" />
<zookeeper incl="zookeeper-servers" optional="true" />
<timezone>Asia/Shanghai</timezone>

conf 下添加配置 metrika.xml
<yandex>
    <clickhouse_remote_servers>
        <!-- 自定义的集群名称 -->
        <ck_cluster>
            <!-- 分片信息 -->
            <shard>
                <!-- 分片负载权重 -->
                <weight>1</weight>
                <!-- 表示副本间是否为内部复制，必须配合复制表引擎使用(建表时指定引擎),​利用zookeeper​​进行数据同步. 
                    默认false 表示insert时向该分片的所有副本中写入数据,失败即丢失(副本间数据一致性不强). -->
                <internal_replication>true</internal_replication>
                <!-- 分片副本信息，这里指定的用户名密码只能是明文 -->
                <replica>
                    <host>node1</host>
                    <port>9000</port>
                    <user>ck_user</user>
                    <!--不能使用加密密码-->
                    <password>root,.123</password>
                </replica>
                <replica>
                    <host>node2</host>
                    <port>19000</port>
                    <user>ck_user</user>
                    <password>root,.123</password>
                </replica>
            </shard>
            <shard>
                <weight>1</weight>
                <internal_replication>true</internal_replication>
                <replica>
                    <host>node2</host>
                    <port>9000</port>
                    <user>ck_user</user>
                    <password>root,.123</password>
                </replica>
                <replica>
                    <host>node3</host>
                    <port>19000</port>
                    <user>ck_user</user>
                    <password>root,.123</password>
                </replica>
            </shard>
            <shard>
                <weight>1</weight>
                <internal_replication>true</internal_replication>
                <replica>
                    <host>node3</host>
                    <port>9000</port>
                    <user>ck_user</user>
                    <password>root,.123</password>
                </replica>
                <replica>
                    <host>node1</host>
                    <port>19000</port>
                    <user>ck_user</user>
                    <password>root,.123</password>
                </replica>
            </shard>
        </ck_cluster>
    </clickhouse_remote_servers>
    <!-- ReplicatedMergeTree引擎依赖zk,有数据写入或者修改时,借助zk的分布式协同能力,实现多个副本之间的同步 -->
    <zookeeper-servers>
        <node index="1">
            <host>node1</host>
            <port>2181</port>
        </node>
        <node index="2">
            <host>node2</host>
            <port>2181</port>
        </node>
        <node index="3">
            <host>node3</host>
            <port>2181</port>
        </node>
    </zookeeper-servers>
    <!-- 建表语句的参数,指定zk的存储目录用,每个节点不同 -->
    <macros>
      <!--集群名称-->
      <layer>ck_cluster</layer>
      <!--分片主从节点相同,整数-->
      <shard>01</shard>
      <!--该节点属于哪个分片的哪个副本-->
      <replica>node01_shard01_replica01</replica>
    </macros>
    <!-- 监听网络-->
    <networks>
        <ip>::/0</ip>
    </networks>
    <!-- 数据压缩算法  -->
    <clickhouse_compression>
        <case>
            <min_part_size>10000000000</min_part_size>
            <min_part_size_ratio>0.01</min_part_size_ratio>
            <method>lz4</method>
        </case>
    </clickhouse_compression>
</yandex>

修改配置后创建从库目录
cp -r main sub
拷贝到其他节点
chmod 777 /home/clickhouse
scp -r /home/clickhouse root@192.168.67.128:/home/

每个节点分别运行

运行主库
docker run -d --rm --name=ck -p 8123:8123 -p 9000:9000 -p 9009:9009 \
--ulimit nofile=262144:262144 \
--volume /home/clickhouse/main/data:/var/lib/clickhouse:rw \
--volume /home/clickhouse/main/conf:/etc/clickhouse-server:rw \
--volume /home/clickhouse/main/log:/var/log/clickhouse-server:rw \
yandex/clickhouse-server:21.3.20

运行从库
docker run -d --rm --name=ck_sub -p 18123:8123 -p 19000:9000 -p 19009:9009 \
--ulimit nofile=262144:262144 \
--volume /home/clickhouse/sub/data:/var/lib/clickhouse:rw \
--volume /home/clickhouse/sub/conf:/etc/clickhouse-server:rw \
--volume /home/clickhouse/sub/log:/var/log/clickhouse-server:rw \
yandex/clickhouse-server:21.3.20

DBeaver驱动, 选择 "添加工件",填写以下三个信息即可,注意版本
<dependency>
    <groupId>ru.yandex.clickhouse</groupId>
    <artifactId>clickhouse-jdbc</artifactId>
    <version>0.2.4</version>
</dependency>
如无法下载驱动,窗口–>首选项–>驱动–>maven–>添加–>添加maven的url 
URL: http://maven.aliyun.com/nexus/content/groups/public/

建表, 必须指定引擎类型，否则就会报Expected one of: storage definition, ENGINE, AS错误。
CREATE TABLE test1
(
    id int,
    name varchar(100),
    birthday date
)
ENGINE = MergeTree
PRIMARY KEY id;

集群验证
SELECT * FROM system.clusters;

在各个节点创建库,建表
create database test_db;

CREATE TABLE test_user
(
    id int,
    name varchar(100),
    birthday date
)
ENGINE = MergeTree
PRIMARY KEY id;

创建分布式表,每个节点执行一次
-- 分布表（Distributed）本身不存储数据，相当于路由，需要指定集群名、数据库名、数据表名、分片KEY.
CREATE TABLE test_db.test_user_all AS test_db.test_user ENGINE = Distributed(ck_cluster, test_db, test_user, rand());

-- 创建分布式表,只用执行一次,目前有问题
CREATE TABLE test_db.test_user_all ON CLUSTER ck_cluster AS test_db.test_user ENGINE = Distributed(ck_cluster, test_db, test_user, rand());

-- 创建复制表, 使用 ReplicatedMergeTree 引擎
在任意节点创建表/新增数据，另一个节点将会通过​​zookeeper​​进行数据同步
create table test_db.t_goods on ck_cluster t_goods_localhost(
    id String,
    prince Float64,
    create_time DateTime
) engine = ReplicatedMergeTree('/clickhouse/tables/{layer}-{shard}/t_goods', '{replica}')
partition by toYYYYMM(create_time)
order by id;
说明
/clickhouse/tables/{layer}-{shard}/t_goods 表示路径, {layer}参数会从<macros>标签中取
{replica}​​宏定义，会引用配置文件中<replica>下面的host


登录clickhouse 端口使用9000
clickhouse-client -h 192.168.67.6 --port 19000 -u 'ck_user' --password 'root,.123'

分布式表清空数据
TRUNCATE table test_user_all on cluster ck_cluster;

插入数据
insert into test_user_all (*) values (1,'lhm','2023-10-01');
insert into test_user_all (*) values (2,'lhm2','2023-10-01');
insert into test_user_all (*) values (3,'lhm3','2023-10-01');
insert into test_user_all (*) values (4,'lhm4','2023-10-01');
insert into test_user_all (*) values (rand32(),generateUUIDv4(),'2023-10-01');

查询本地数据,及所有节点数据
select * from test_user;
select * from test_user_all;
-- 生成uuid
select generateUUIDv4();
select rand32();

可安装zk-web可视化界面,查看zookeeper信息