package com.oldwei.hikdev.runner;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.system.OsInfo;
import cn.hutool.system.SystemUtil;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.oldwei.hikdev.entity.config.DeviceSearchInfo;
import com.oldwei.hikdev.entity.config.DeviceSearchInfoDTO;
import com.oldwei.hikdev.mqtt.MqttConnectClient;
import com.oldwei.hikdev.service.IHikAlarmDataService;
import com.oldwei.hikdev.service.IHikDeviceService;
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

    private final IHikDeviceService hikDeviceService;
    private final IHikAlarmDataService hikAlarmDataService;

    @Value("${mqtt.enable}")
    private boolean mqttEnable;

    @Override
    public void run(ApplicationArguments args) {
        OsInfo osInfo = SystemUtil.getOsInfo();
        if (mqttEnable) {
            //实例化bean之后 初始化连接
            this.mqttConnectClient.initMqttClient();
            this.mqttConnectClient.mqttConnect();
        }
        ThreadUtil.execAsync(() -> {
            // 项目启动后应该把已存在的设备进行登录、布防
            List<DeviceSearchInfo> deviceSearchInfoList = ConfigJsonUtil.getDeviceSearchInfoList();
            deviceSearchInfoList.forEach(d -> {
                d.setAlarmHandleId(-1);
                d.setPreviewHandleId(-1);
                d.setLoginId(-1);
            });
            JSONObject configJson = ConfigJsonUtil.readConfigJson();
            configJson.put(ConfigJsonUtil.deviceSearchInfo, deviceSearchInfoList);
            ConfigJsonUtil.writeConfigJson(configJson.toJSONString(JSONWriter.Feature.PrettyFormat, JSONWriter.Feature.WriteMapNullValue));
            deviceSearchInfoList.forEach(d -> {
                if (StrUtil.isNotBlank(d.getUsername()) && StrUtil.isNotBlank(d.getPassword())) {
                    boolean login = this.hikDeviceService.login(d.findDeviceLoginDTO());
                    if (login) {
                        this.hikAlarmDataService.setupAlarmChan(d.getIpv4Address());
                    }
                }
            });
        });
        ThreadUtil.execAsync(() -> {
            // 启动rtsp_server服务
            if (osInfo.isWindows()) {
                RuntimeUtil.exec("cmd /c cd " + System.getProperty("user.dir") + "/rtsp_server/rtsp_server_windows/ && rtsp_server");
            } else if (osInfo.isLinux()) {
                //start rtsp server
//                RuntimeUtil.exec("cd rtsp_server/rtsp_server_linux");
//                RuntimeUtil.exec("cd /home/oem/IdeaProjects/hik-dev/rtsp_server/rtsp_server_linux && ./rtsp_server");
                String command = "cd /home/oem/IdeaProjects/hik-dev/rtsp_server/rtsp_server_linux && ./rtsp_server";
                try {
                    Process process = Runtime.getRuntime().exec(command);
                    int waitFor = process.waitFor();
                    if (waitFor>= 0) {
                        log.info("调用成功");
                    } else {
                        log.info("调用失败");
                    }
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        ThreadUtil.execAsync(() -> {
            try (MulticastSocket multicastSocket = new MulticastSocket(37020)) {
                InetAddress inetAddress = InetAddress.getByName("239.255.255.250");
                DatagramPacket datagramPacket = new DatagramPacket(new byte[4096], 4096);

                // 遍历查找IPv4的网卡
                NetworkInterface.networkInterfaces().forEach(networkInterface -> {
                    int port = 20220;
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
                        // 过滤无效数据
                        if (stringObjectMap.containsKey("IPv4Address")) {
                            DeviceSearchInfoDTO deviceSearchInfoDTO = BeanUtil.toBean(stringObjectMap, DeviceSearchInfoDTO.class);
                            ConfigJsonUtil.saveOrUpdateDeviceSearch(deviceSearchInfoDTO);
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
