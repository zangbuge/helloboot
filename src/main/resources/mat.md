### 一. MAT工具
1. 选择1.8.1版本对应jdk1.8, 解压后可直接运行,菜单open heap dump 打开堆快照文件  http://www.eclipse.org/mat/previousReleases.php
2. 启动项目时设置jvm参数,然后应用启动后出现内存异常则会自动导出dump文件，默认的文件名是：java_pid<进程号>.hprof。
获取dump文件必须是一出现内存异常就获取实时的dump文件，这样获取的文件信息才比较准确，
如果过了一段时间在导出dump文件，就会因gc的缘故，导致信息不准确
```$xslt
 -XX:+HeapDumpOnOutOfMemoryError    #开启堆快照,当JVM发生OOM时，自动生成DUMP文件
 -XX:HeapDumpPath=D:\dump           #保存文件目录,不指定文件名默认在项目根目录下生成格式为: java_<pid>_<date>_<time>_heapDump.hprof
 #还可再增加触发生成dump文件的条件
 -XX:+HeapDumpBeforeFullGC		#当JVM 执行 FullGC前
 -XX:+HeapDumpAfterFullGC		#当JVM 执行 FullGC后
```
3. jmap命令获取实时jvm堆快照,内存映像工具
```$xslt
jmap -dump:format=b,file=./heap_dump_temp.hprof pid
# 成功返回: Heap dump file created
```
使用eclipse memory analyzer(内存分析器工具)的dump分析工具打开.hprof文件
    Histogram：直方图:列出每个类的实例数量
    Dominator Tree：支配树:列出最大的对象和它们保持存活的对象。
    Top Consumers：内存高占比消费对象：打印消耗最多的对象，并按类和包分组。
    Duplicate Classes：重复类：检测由多个类装载装载的类。
    Leak Suspects：泄漏疑点:包括泄漏疑点和系统概述
3.1 在"Reports"板块点击"Leak Suspects" (内存泄漏嫌疑人)
    然后选中怀疑的板块点击"See stacktrace" 即可查看对应的 "Thread Stack" (线程堆栈)
3.2 主面板"Actions" > biggest objects 查看大对象清单, expensive objects 查看昂贵对象清单
    对象上右键 List object 
                    with outgoing references  (当前对象引用的对象)
                    with incomming references (引用当前对象的对象)
    Shallow Heap  (浅堆): 对象自身占用内存大小
    Retained Heap (深堆): 对象自身占用内存大小 + 引用其他对象占用内存大小
3.3 Java 应用内存占用大,导出的dump 堆内存很少
    内存泄漏是导致 Java 应用程序内存占用过大的重要原因之一。它指的是应用程序中的对象占用了内存，但却无法被垃圾回收器回收，
    从而导致内存占用不断增加。最常见的内存泄漏场景是对象的引用意外地被保留了下来。
    例如，当我们使用一个全局的集合来存储对象时，如果我们忘记从集合中删除对象的引用，那么这些对象将一直占用内存。
```aidl
public class MemoryLeakExample {
    private static List<String> list = new ArrayList<>();
    public static void main(String[] args) {
        for (int i = 0; i < 1000000; i++) {
            String data = "Data" + i;
            list.add(data); // 添加对象到集合中
        }
    }
}
```

3.4 mat工具无法打开太大的dump文件, 修改根目录下的.ini文件. 修改最大内存, 默认 1024M

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
    S0：Survivor0 区占用百分比 (年轻代中第一个幸存区)
    S1：Survivor1 区占用百分比 (年轻代中第二个幸存区)
    E：Eden 区占用百分比 (年轻代中Eden)
    O：老年代占用百分比 
    M：元数据区占用百分比 (即方法区主要用于存储类的信息、常量池、方法数据、方法代码等,为了与堆进行区分，通常又叫"非堆")
    YGC：年轻代回收次数(从应用程序启动到采样时年轻代中gc次数)
    YGCT：年轻代回收耗时
    FGC：老年代回收次数(从应用程序启动到采样时old代(full gc)gc次数)
    FGCT：老年代回收耗时
    GCT：GC总耗时 (从应用程序启动到采样时gc用的总时间s)

Java虚拟机中，内存分为三个代：新生代(New)、老生代(Old)、永久代(Perm)
新生代New：新建的对象都存放这里
老生代Old：存放从新生代New中迁移过来的生命周期较久的对象。新生代New和老生代Old共同组成了堆内存
永久代Perm：是非堆内存的组成部分。主要存放加载的Class类级对象如class本身，method，field等

新生代分为：伊甸园区：幸存者0：幸存者1 = 8:1:1, 新生代默认占整堆的1/3
新建的对象 存放在伊甸园区，第一次垃圾回收时，垃圾对象直接被回收掉，存活下来的对象会被存放到幸存者0区或幸存者1区
再次垃圾回收时会把幸存者0区（1区）存活的对象移动到幸存者1区（0区），然后将幸存者0区（1区）清空，依次交替执行。
每次保证有一个幸存者区域是空的，内存是完整的。
当对象经过15（阈值）次垃圾回收后，依然存活的对象将会被移动到老年区（老年区垃圾回收的频率会比较低）

分代收集思想Minor GC、Major GC、Full GC
一般情况下收集新生代 Minor GC/Yong GC
当老年代的空间不足时 会触发Major GC/Old GC
整堆收集 Full GC, 尽量避免, full gc时会暂停其他线程工作

整堆收集触发的条件:
    System.gc();时
    老年区空间不足
    方法区空间不足

4. jstack 用于生成java虚拟机当前时刻的线程快照
生成线程快照的主要目的是定位线程出现长时间停顿的原因，
如线程间死锁,死循环,CPU使用过高,请求外部资源导致的长时间等待等问题
jstack -l pid  #除堆栈外,显示关于锁的附加信息, 可查看到线程状态 waiting,runnable等
jstack -F pid  #强制输出线程堆栈 (进程挂起而没有任何响应，那么可以使用 -F 参数)

查看进程下哪些线程占用了高的cpu 
top
Shift + M  可以按照内存使用量进行排序
top c  --可以显示具体那个应用
或使用 ps -ef|grep pid 看具体应用服务
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
df -h  # / 路径是系统盘大小; /data 数据盘大小
du -sh * | sort -hr #查看当前目录各文件大小,降序

查看内存使用情况
free -h #其中 -h 参数表示单位GB
    total(总计)   used(已用)    free(空闲)    shared(共享)  buff/cache(缓冲/缓存)   available(可用)
    62G             43G            3.5G         3.1            15G                      15G
Swap: 0B            0B              0B

total 物理内存或交换分区的总量
used  当前被使用的内存量(包括应用程序和缓存)
free  完全未被使用的内存量（未被任何进程或缓存占用）
shared 多个进程共享的内存量
buff/cache	被内核缓冲区（buffers）和页面缓存（cache）占用的内存（可被快速释放）
available 估算的剩余可用内存量（包括未被使用的内存和可回收的缓存）
Swap(交换分区) 当物理内存不足时，系统会将不常用的内存页交换到磁盘上的交换分区（或交换文件）
当Swap的used 为 0：表示当前未使用交换分区，内存压力较低;频繁使用 Swap：可能表明物理内存不足，需优化应用或增加内存

使用场景
1.检查内存是否充足 观察 available 字段是否持续较低（如 <10%），可能需优化应用或扩容
2.监控缓存效率 高 buff/cache 和低 swap used 表明系统高效利用了缓存，磁盘 I/O 压力小
3.排查内存泄漏  若 available 持续下降且 free 不变，可能存在内存泄漏（缓存未被释放）


查看机器硬件核心线程数
lscpu
CPU(s): 16              #逻辑核心总数
Thread(s) per core: 2   #每个核心的线程数
Core(s) per socket: 1   #每个物理CPU的核心数
Socket(s): 8            #物理CPU颗数

### 三. GCViewer 
3.1 GC日志工具 github下载 gcviewer-1.36.jar 官方版本: https://github.com/chewiebug/GCViewer/releases
可打开GC日志文件 view 设置
Full GC Lines： （full gc）
Inc GC Lines：（增量GC）
GC Times Line： （gc 时间）
GC Times Rectangles： （gc时间区域）
Total Heap：(总堆大小)
Red line that shows heap size
Tenured Generation：（老年代）
Young Generation：（年轻代）
Used Heap：（堆使用量）
Initial mark level：（cms或g1垃圾回收算法初始标记事件）
Concurrent collections：（cms或g1垃圾回收并发收集周期）

3.2 输出 GC 日志详情
-verbose:gc   #打印每次gc信息
-XX:+UseG1GC  #使用G1回收器
-XX:MaxGCPauseMillis=200　＃每次GC内存回收花费时间不超过设定值单位毫秒，默认情况下，VM没有暂停时间目标值。GC的暂停时间主要取决于堆中实时数据的数量与实时数据量
-XX:+PrintGCDetails  #打印GC日志详情
-XX:+PrintGCTimeStamps    ＃打印CG发生的时间
-XX:+PrintGCCause         #打印触发GC原因信息
-XX:+PrintGCApplicationStoppedTime #打印应用由于GC而产生的停顿时间
-Xloggc:gc/jvm_gc.log                  #日志输出位置,目录需提前创建
-XX:+UseGCLogFileRotation           #开启日志文件分割
-XX:NumberOfGCLogFiles=30           #最大30个
-XX:GCLogFileSize=500M              #每个文件最大500M
-Duser.timezone=Asia/Shanghai
-Dfile.encoding=UTF-8

3.3 jvm参数
```$xslt
-Xms1024m -Xmx1024m -Xmn256m -Xss256k -verbose:gc -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintGCCause -XX:+PrintGCApplicationStoppedTime -Xloggc:jvm_gc.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=30 -XX:GCLogFileSize=500M 
```

3.4 操作系统 与 JVM的内存分配
JVM的垃圾回收,只是一个逻辑上的回收,回收的只是JVM申请的那一块逻辑堆区域,将数据标记为空闲之类的操作,不是调用free将内存归还给操作系统
其实只是先向操作系统申请了一大块内存,然后自己在这块已申请的内存区域中进行“自动内存管理”. 
JAVA 中的对象在创建前,会先从这块申请的一大块内存中划分出一部分来给这个对象使用,在 GC 时也只是这个对象所处的内存区域数据清空,标记为空闲而已.
JVM 还是会归还内存给操作系统的，只是因为这个代价比较大，所以不会轻易进行。而且不同垃圾回收器 的内存分配算法不同，归还内存的代价也不同
问: 那是不是只要我把 Xms 和 Xmx 配置成一样的大小，这个 JAVA 进程一启动就会占用这个大小的内存呢?
答: 不会的，哪怕你 Xms6G，启动也只会占用实际写入的内存，大概率达不到 6G，
可以简单的认为操作系统的内存分配是“惰性”的，分配并不会发生实际的占用，有数据写入时才会发生内存占用，影响 Res
启动就分配个最大内存，避免它运行中扩容影响服务；所以一般 JAVA 程序还会将 Xms和Xmx配置为相等的大小，避免这个扩容的操作