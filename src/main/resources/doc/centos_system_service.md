-- 列出正在运行的系统服务 test_app.service 可不加运行状态条件
systemctl list-units --type=service --state=running
-- 查看自启动的服务
systemctl list-unit-files --type=service |grep enabled
-- 开机自启动 status 查状态
systemctl enable  service_name
-- 禁止自启动
systemctl disable  service_name
-- 启动服务
systemctl start test_app.service

-- 系统服务配置位置地址
cd /etc/systemd/system/
-- 或放在这里 Systemd的默认启动服务集合
default.target -> /lib/systemd/system/multi-user.target
-- 该目录下可添加自定义服务, 定义服务: 分为 [Unit], [Service], [Install]三个小节. RestartSec 重启间隔时间，单位是秒. EnvironmentFile 通过文件定义程序运行时的环境变量
cd /lib/systemd/system/
vim test_app.service
```aidl
[Unit]
Description=test_app

[Service]
Type=simple
EnvironmentFile=/usr/local/etc/my_app.env
WorkingDirectory=/opt/test_app
ExecStart=/bin/sh ${WorkingDirectory}/start.sh
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
```
