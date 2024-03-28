部署ck 3分片2副本, 共6个ck服务, 3个zk节点
节点
192.168.10.128
192.168.10.130
192.168.10.132

config.xml文件需要修改,添加如下信息,删除原始的remote_servers,zookeeper标签
<!-- 外部依赖配置文件 -->
<include_from>/etc/clickhouse-server/metrika.xml</include_from>
<!-- 集群相关的配置，可以用外部依赖文件来配置 -->
<remote_servers incl="clickhouse_remote_servers" optional="true" />
<zookeeper incl="zookeeper-servers" optional="true" />
<timezone>Asia/Shanghai</timezone>

conf下添加配置metrika.xml
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
                    <host>192.168.67.6</host>
                    <port>9000</port>
                    <user>ck_user</user>
                    <!--不能使用加密密码-->
                    <password>root,.123</password>
                </replica>
                <replica>
                    <host>192.168.67.128</host>
                    <port>19000</port>
                    <user>ck_user</user>
                    <password>root,.123</password>
                </replica>
            </shard>
            <shard>
                <weight>1</weight>
                <internal_replication>true</internal_replication>
                <replica>
                    <host>192.168.67.128</host>
                    <port>9000</port>
                    <user>ck_user</user>
                    <password>root,.123</password>
                </replica>
                <replica>
                    <host>192.168.67.3</host>
                    <port>19000</port>
                    <user>ck_user</user>
                    <password>root,.123</password>
                </replica>
            </shard>
            <shard>
                <weight>1</weight>
                <internal_replication>true</internal_replication>
                <replica>
                    <host>192.168.67.3</host>
                    <port>9000</port>
                    <user>ck_user</user>
                    <password>root,.123</password>
                </replica>
                <replica>
                    <host>192.168.67.6</host>
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
            <host>192.168.67.6</host>
            <port>2181</port>
        </node>
        <node index="2">
            <host>192.168.67.128</host>
            <port>2181</port>
        </node>
        <node index="3">
            <host>192.168.67.3</host>
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

每个节点分别运行
docker run -d --rm --name=ck -p 8123:8123 -p 9000:9000 -p 9009:9009 \
--ulimit nofile=262144:262144 \
--volume /home/clickhouse/data:/var/lib/clickhouse:rw \
--volume /home/clickhouse/conf:/etc/clickhouse-server:rw \
--volume /home/clickhouse/log:/var/log/clickhouse-server:rw \
yandex/clickhouse-server:21.3.20

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