# 海康威视 设备网络SDK_Win64 Java二次开发

### 硬件产品

硬件产品包括网络硬盘录像机、网络摄像机、网络球机、出入口产品、门禁、可视对讲、热成像、移动车载、报警主机、显示控制产品、网络存储等产品。

下载地址:

选择硬件产品 -> 设备网络SDK

[https://open.hikvision.com/download](https://open.hikvision.com/download)

### 指南

设备网络SDK是基于设备私有网络通信协议开发的，为海康威视各类硬件产品服务的配套模块，用于远程访问和控制设备的软件二次开发，内含SDK动态库、开发文档 及Demo示例（C++、C#、Java）。

### 运行

```dockerfile
docker build -t hik-dev:latest . && docker run -p 8923:8923 -v /Users/oldwei/IdeaProjects/hik-dev/:/opt/hik-dev --name hik-dev hik-dev:latest
```

IDEA配置如下：

![img.png](img/img.png)
### 开发计划

| 计划功能        | 完成情况 |
|-------------|------|
| 设备注册(登录)    | 完成✅  |
| 局域网发现（扫描）设备 | 完成✅  |
| 获取门禁人员列表    | 完成✅  |
| 获取门禁存储的人脸信息 | 完成✅  |
| 下发门禁卡       | 完成✅  |
| 下发门禁人脸      | 完成✅  |
| 门禁事件布防      | 完成✅  |
| 门禁事件上传(含照片) | 完成✅  |
| 获取设备当前帧画面   | 完成✅  |
| 摄像机rtsp推流   | 优化   |
| 摄像机sdk推流    | 优化   |

### MQTT配置

默认关闭状态

如果要使用mqtt，请将springboot配置文件中mqtt.enable设置为true

### 文档

项目启动后会自动搜索局域网内的设备，每隔一分钟搜索一次；也会检查当前json文件中存在的设备信息，如果有账号密码会默认进行一次登录和布防的操作。

### 打赏

如果觉得本项目对你有帮助，请点一个star★

感谢你的支持！！！