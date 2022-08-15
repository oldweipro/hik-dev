package com.oldwei.hikdev.runner;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import com.alibaba.fastjson2.JSONObject;
import com.oldwei.hikdev.mqtt.MqttConnectClient;
import com.oldwei.hikdev.util.ConfigJsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description 实现 ApplicationRunner 接口后项目启动时会按照执行顺序执行 run 方法
 * @Author oldwei
 * @Date 2019/8/23 11:39
 * @Version 2.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StartedUpRunner implements ApplicationRunner {
    private final MqttConnectClient mqttConnectClient;

    @Value("${mqtt.enable}")
    private boolean mqttEnable;

    @Override
    public void run(ApplicationArguments args) {
        if (mqttEnable) {
            //实例化bean之后 初始化连接
            this.mqttConnectClient.initMqttClient();
            this.mqttConnectClient.mqttConnect();
        }
        ThreadUtil.execAsync(() -> {
            try (MulticastSocket multicastSocket = new MulticastSocket(37020)) {
                InetAddress inetAddress = InetAddress.getByName("239.255.255.250");
                DatagramPacket datagramPacket = new DatagramPacket(new byte[4096], 4096);

                // 遍历查找IPv4的网卡
                NetworkInterface.networkInterfaces().forEach(networkInterface -> {
                    int port = 38888;
                    Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress inetAddressElement = inetAddresses.nextElement();
                        try {
                            if (inetAddressElement instanceof Inet4Address && !networkInterface.isLoopback()) {
                                // 加入多播组
                                InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddress, port);
                                multicastSocket.joinGroup(inetSocketAddress, networkInterface);
                                port++;
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                while (true) {
                    // 接收数据，在ScheduledTask类中会有一个定时任务searchHikDevice，每一分钟发送一次请求，所以这里每隔一分钟会收到一份响应
                    multicastSocket.receive(datagramPacket);
                    byte[] receiveByte = Arrays.copyOfRange(datagramPacket.getData(), 0, datagramPacket.getLength());
                    String decodeData = new String(receiveByte, StandardCharsets.UTF_8).trim();
                    try {
                        Map<String, Object> stringObjectMap = XmlUtil.xmlToMap(decodeData);
                        JSONObject xmlToMap = JSONObject.parseObject(JSONObject.toJSONString(stringObjectMap));
                        // 过滤无效数据
                        if (xmlToMap.containsKey("DeviceSN")) {
                            System.out.println(xmlToMap);
                            // 这会有一个并行问题，如果同时打开文件，第一个人修改之后保存为A，第二个人修改之后保存为B，这个内容最终会覆盖A，显示为B内容，目前的业务一般不会出现
                            JSONObject configJson = ConfigJsonUtil.readConfigJson();
                            if (ObjectUtil.isNull(configJson)) {
                                configJson = new JSONObject();
                            }
                            // 根据设备的DeviceSN判断设备是否存在，如果设备已存在，更新此设备；设备不存在，新增设备

                            List<JSONObject> devices = new ArrayList<>();
                            if (configJson.containsKey("devices")) {
                                devices = configJson.getJSONArray("devices").toList(JSONObject.class);
                            }
                            if (devices.size() > 0) {
                                // 判断当前json文件中是否记录改序列号DeviceSN
                                List<JSONObject> collect = devices.stream().filter(device -> StrUtil.equals(device.getString("DeviceSN"), xmlToMap.getString("DeviceSN"))).collect(Collectors.toList());
                                if (collect.size() > 0) {
                                    // 说明已存在，需要进行更新
                                    devices.forEach(device -> {
                                        if (StrUtil.equals(device.getString("DeviceSN"), xmlToMap.getString("DeviceSN"))) {
                                            device.putAll(xmlToMap);
                                        }
                                    });
                                } else {
                                    // 不存在，直接新增
                                    devices.add(xmlToMap);
                                }
                            } else {
                                devices.add(xmlToMap);
                            }
                            configJson.put("devices", devices);
                            boolean b = ConfigJsonUtil.writeConfigJson(configJson.toJSONString());
                            if (b) {
                                log.info("修改配置文件成功");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        log.info("=========================项目启动完成=========================");
    }
}
