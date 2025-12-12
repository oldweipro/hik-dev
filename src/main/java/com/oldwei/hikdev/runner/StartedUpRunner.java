package com.oldwei.hikdev.runner;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.oldwei.hikdev.entity.config.DeviceSearchInfo;
import com.oldwei.hikdev.entity.config.DeviceSearchInfoDTO;
import com.oldwei.hikdev.service.IHikAlarmDataService;
import com.oldwei.hikdev.service.IHikDeviceService;
import com.oldwei.hikdev.util.ConfigJsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InterruptedIOException;
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
    private final IHikDeviceService hikDeviceService;
    private final IHikAlarmDataService hikAlarmDataService;

    @Override
    public void run(ApplicationArguments args) {
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
            MulticastSocket multicastSocket = null;
            try {
                // 监听指定多播端口
                multicastSocket = new MulticastSocket(37020);
                InetAddress multicastGroup = InetAddress.getByName("239.255.255.250");

                // 获取所有网络接口（Java 1.8 兼容）
                Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
                while (networkInterfaces.hasMoreElements()) {
                    NetworkInterface ni = networkInterfaces.nextElement();
                    if (ni.isLoopback() || !ni.isUp()) {
                        continue; // 跳过回环和未启用的接口
                    }

                    Enumeration<InetAddress> addresses = ni.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress addr = addresses.nextElement();
                        if (addr instanceof Inet4Address) {
                            try {
                                // 加入多播组：端口必须与 MulticastSocket 的端口一致（37020）
                                // 注意：InetSocketAddress 的端口在这里其实会被忽略（JDK 实现中只用 IP）
                                // 但为语义清晰，建议使用 multicastSocket.getLocalPort()
                                InetSocketAddress group = new InetSocketAddress(multicastGroup, multicastSocket.getLocalPort());
                                multicastSocket.joinGroup(group, ni);
                                // 可选：记录已加入的接口
                                // log.debug("Joined multicast group on interface: {}", ni.getName());
                            } catch (IOException e) {
                                // 部分接口可能不支持多播（如某些虚拟网卡），记录但不中断
                                // log.warn("Failed to join multicast on interface: {}", ni.getName(), e);
                            }
                        }
                    }
                }

                byte[] buffer = new byte[4096];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                // 持续接收数据
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        multicastSocket.receive(packet);
                        String xmlData = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8).trim();

                        Map<String, Object> map = XmlUtil.xmlToMap(xmlData);
                        if (map != null && map.containsKey("IPv4Address")) {
                            DeviceSearchInfoDTO dto = BeanUtil.toBean(map, DeviceSearchInfoDTO.class);
                            ConfigJsonUtil.saveOrUpdateDeviceSearch(dto);
                        }
                    } catch (InterruptedIOException e) {
                        // 超时或中断，可选处理
                        break;
                    } catch (Exception e) {
                        // log.warn("Failed to process multicast packet", e);
                    } finally {
                        packet.setLength(buffer.length); // 重置长度，避免下一次接收出错
                    }
                }

            } catch (IOException e) {
                // log.error("Multicast receiver error", e);
            } finally {
                if (multicastSocket != null) {
                    try {
                        multicastSocket.close();
                    } catch (Exception ignored) {}
                }
            }
        });
        log.info("=========================项目启动完成=========================");
    }
}
