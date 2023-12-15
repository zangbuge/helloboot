1. 安装tcpdump
yum install -y tcpdump

2. 使用方法
抓取所有经过 网卡为 ens33 目的或源地址是 192.168.1.2 的网络数据 ，并且保存到 xx.pcap 文件中
tcpdump -i ens33 host 192.168.79.128  -w xx.pcap

抓取网口1 目的或源地址端口是9200的数据，保存到xx.pcap中
tcpdump -i ens33 port 9200 -vvv  -w xx.pcap

3. 使用WireShark工具分析.pcap文件， 可看到http请求及相应
