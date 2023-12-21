### 一. MAT工具
1. 选择1.8.1版本对应jdk1.8, 解压后可直接运行,菜单open heap dump 打开堆快照文件  http://www.eclipse.org/mat/previousReleases.php
2. 启动项目时设置jvm参数,然后应用启动后出现内存异常则会自动导出dump文件，默认的文件名是：java_pid<进程号>.hprof。
获取dump文件必须是一出现内存异常就获取实时的dump文件，这样获取的文件信息才比较准确，
如果过了一段时间在导出dump文件，就会因gc的缘故，导致信息不准确
```$xslt
 -XX:+HeapDumpOnOutOfMemoryError    #开启堆快照
 -XX:HeapDumpPath=D:\dump           #保存文件到哪个目录
```
3. jmap命令获取实时jvm堆快照,内存映像工具
```$xslt
jmap -dump:format=b,file=./dumptest.hprof pid
# 成功返回: Heap dump file created
```

4. OOM一般有以下两种情况
4.1 年老代堆空间被占满
说明：这是最典型的内存泄漏方式，简单说就是所有堆空间都被无法回收的垃圾对象占满，
虚拟机再也无法分配新空间
解决方案：这种方式解决起来比较简单，一般就是根据垃圾回收前后的情况对比，
同时根据对象引用情况（常见的集合对象引用）分析，基本都可以找到泄漏点
4.2 持久代被占满
Perm 空间被占满，无法为新的 class 分配存储空间而引发的异常。
这个异常以前是没有的，但是在 java 大量使用反射的今天这个异常就比较常见了。
主要原因是大量动态反射生成的类不断被加载，最终导致 Perm 区被占满。
更可怕的是，不同的 classLoader 即便使用相同的类，但是都会对其进行加载，
相当于同一个东西，如果有 N 个classLoader 那么它将会被加载 N 次。
因此，在某些情况下，这个问题基本视为无解，当然，存在大量 classLoader 和大量反射类的情况并不多
解决方案：增加持久代内存 ，例如：-XX：MaxPermSize=16M

### 二. JDK的命令行工具
1. jps [options] 查看当前jvm在运行的应用
jps -l 输出主类全名或jar路径
jps -m 输出JVM启动时传递给main()的参数
jps -v 输出JVM启动时显示指定的JVM参数

2. jinfo [option] pid  查看java应用配置参数 
-flag     输出指定args参数的值
-flags    不需要args参数，输出所有JVM参数的值
-sysprops 输出系统属性，等同于System.getProperties()

3. jstat [option] pid 
-class  监视类装载、卸载数量、总空间以及类装载所耗费的时间
-gc     监视Java堆状况，包括Eden区、两个Survivor区、老年代、永久代等的容量、已用空间、GC时间合计等信息
    S0C：年轻代中 To Survivor 的容量（单位 KB）；
    S1C：年轻代中 From Survivor 的容量（单位 KB）；
    S0U：年轻代中 To Survivor 目前已使用空间（单位 KB）；
    S1U：年轻代中 From Survivor 目前已使用空间（单位 KB）；
    EC：年轻代中 Eden 的容量（单位 KB）；
    EU：年轻代中 Eden 目前已使用空间（单位 KB）；
    OC：老年代的容量（单位 KB）；
    OU：老年代目前已使用空间（单位 KB）；
    MC：Metaspace 的容量（单位 KB）；
    MU：Metaspace 目前已使用空间（单位 KB）；
    CCSC：压缩类空间大小
    CCSU：压缩类空间使用大小
    YGC：从应用程序启动到采样时年轻代中 gc 次数；
    YGCT：从应用程序启动到采样时年轻代中 gc 所用时间 (s)；
    FGC：从应用程序启动到采样时 old 代（全 gc）gc 次数；
    FGCT：从应用程序启动到采样时 old 代（全 gc）gc 所用时间 (s)；
    GCT：从应用程序启动到采样时 gc 用的总时间 (s)
    
-gcutil 查看垃圾回收统计
    S0：Survivor0 区占用百分比
    S1：Survivor1 区占用百分比
    E：Eden 区占用百分比
    O：老年代占用百分比
    M：元数据区占用百分比
    YGC：年轻代回收次数
    YGCT：年轻代回收耗时
    FGC：老年代回收次数
    FGCT：老年代回收耗时
    GCT：GC总耗时
 
4. jstack 用于生成java虚拟机当前时刻的线程快照
生成线程快照的主要目的是定位线程出现长时间停顿的原因，
如线程间死锁,死循环,CPU使用过高,请求外部资源导致的长时间等待等问题
jstack -l pid  #除堆栈外,显示关于锁的附加信息, 可查看到线程状态 waiting,runnable等
jstack -F pid  #强制输出线程堆栈 (进程挂起而没有任何响应，那么可以使用 -F 参数)

查看进程下哪些线程占用了高的cpu 
top
top c  --可以显示具体那个应用
查看某个进程pid下占用最高的线程pid (docker容器内不支持,可执行命令htop 进到htop页面按shift+p按照CPU使用率排序)
top -Hp pid 
将十进制线程pid转换为十六进制
printf "%x" 1700
导出线程快照, 用十六进制在jstack_info.txt中查找 nid=十六进制
jstack pid > jstack_info.txt 

5. bin/jconsole.exe jdk自带监视与管理控制台,可监控远程进程
应用添加启动参数后支持 hostname为服务器的ip非客户端, authenticate: false不用登录
-Djava.rmi.server.hostname=192.168.79.128
-Dcom.sun.management.jmxremote
-Dcom.sun.management.jmxremote.port=12345
-Dcom.sun.management.jmxremote.ssl=false
-Dcom.sun.management.jmxremote.authenticate=false


查看磁盘使用情况
df -h

查看内存使用情况
free -h