### MAT工具
1. 选择1.8.1版本对应jdk1.8, 解压后可直接运行,菜单open heap dump 打开堆快照文件  http://www.eclipse.org/mat/previousReleases.php
2. 启动项目时设置jvm参数,然后应用启动后出现内存异常则会自动导出dump文件，默认的文件名是：java_pid<进程号>.hprof。
获取dump文件必须是一出现内存异常就获取实时的dump文件，这样获取的文件信息才比较准确，
如果过了一段时间在导出dump文件，就会因gc的缘故，导致信息不准确
```$xslt
 -XX:+HeapDumpOnOutOfMemoryError    #开启堆快照
 -XX:HeapDumpPath=D:\dump           #保存文件到哪个目录
```
3. jmap命令获取实时jvm情况命令
```$xslt
jmap -dump:format=b,file=D:/dump/oom.hprof pid
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



