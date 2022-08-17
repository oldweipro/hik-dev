package com.oldwei.hikdev.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.oldwei.hikdev.entity.config.DeviceLoginDTO;
import com.oldwei.hikdev.entity.config.DeviceSearchInfo;
import com.oldwei.hikdev.entity.config.DeviceSearchInfoDTO;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author oldwei
 * @date 2022/8/12 23:44
 */
public class ConfigJsonUtil {
    static final String configPath = System.getProperty("user.dir") + "/sdk/config/config.json";
    static final String deviceSearchInfo = "deviceSearchInfo";

    public static JSONObject readConfigJson() {
        boolean exist = FileUtil.exist(configPath);
        JSONObject jsonObject = new JSONObject();
        if (exist) {
            FileReader fileReader = new FileReader(configPath);
            jsonObject = JSON.parseObject(fileReader.readString());
            if (ObjectUtil.isNull(jsonObject)) {
                jsonObject = new JSONObject();
            }
        }
        return jsonObject;
    }

    public static boolean writeConfigJson(String json) {
        boolean exist = FileUtil.exist(configPath);
        if (!exist) {
            FileUtil.touch(configPath);
        }
        FileWriter writer = new FileWriter(configPath);
        try {
            writer.write(json);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean saveOrUpdateDeviceLogin(DeviceLoginDTO deviceLogin) {
        // 判断json文件中是否有deviceLogin数据 && 判断这个数据是否存在
        List<DeviceSearchInfo> deviceLoginLoginList = getDeviceSearchInfoList();
        if (deviceLoginLoginList.size() > 0 && ObjectUtil.isNotNull(getDeviceSearchInfoByIp(deviceLogin.getIpv4Address()))) {
            deviceLoginLoginList.forEach(d -> {
                if (StrUtil.equals(d.getIpv4Address(), deviceLogin.getIpv4Address())) {
//                    BeanUtil.copyProperties(deviceLogin, d);
                    d.setDeviceLoginDTO(deviceLogin);
                }
            });
        } else {
            DeviceSearchInfo deviceSearchInfo = new DeviceSearchInfo();
            deviceSearchInfo.setDeviceLoginDTO(deviceLogin);
            deviceLoginLoginList.add(deviceSearchInfo);
        }
        JSONObject configJson = readConfigJson();
        configJson.put(ConfigJsonUtil.deviceSearchInfo, deviceLoginLoginList);
        ConfigJsonUtil.writeConfigJson(configJson.toJSONString());
        return true;
    }

    /**
     * 将设备登录状态重置为-1：表示未注册（未登录）
     * @param ip
     * @return
     */
    public static boolean removeDeviceLogin(String ip) {
        // 判断json文件中是否有deviceLogin数据 && 判断这个数据是否存在
        List<DeviceSearchInfo> deviceLoginLoginList = getDeviceSearchInfoList();
        if (deviceLoginLoginList.size() > 0 && ObjectUtil.isNotNull(getDeviceSearchInfoByIp(ip))) {
            deviceLoginLoginList.forEach(d -> {
                if (StrUtil.equals(d.getIpv4Address(), ip)) {
                    d.setLoginId(-1);
                }
            });
        } else {
            return false;
        }
        JSONObject configJson = readConfigJson();
        configJson.put(ConfigJsonUtil.deviceSearchInfo, deviceLoginLoginList);
        ConfigJsonUtil.writeConfigJson(configJson.toJSONString());
        return true;
    }

    public static List<DeviceSearchInfo> getDeviceSearchInfoList() {
        JSONObject configJson = readConfigJson();
        List<DeviceSearchInfo> deviceSearchInfoList = new ArrayList<>();
        if (configJson.containsKey(deviceSearchInfo)) {
            deviceSearchInfoList = configJson.getJSONArray(deviceSearchInfo).toList(DeviceSearchInfo.class);
        }
        return deviceSearchInfoList;
    }

    public static DeviceSearchInfo getDeviceSearchInfoByIp(String ip) {
        List<DeviceSearchInfo> collect = getDeviceSearchInfoList().stream().filter(d -> StrUtil.equals(d.getIpv4Address(), ip)).collect(Collectors.toList());
        if (collect.size() > 0) {
            return collect.get(0);
        }
        return new DeviceSearchInfo();
    }

    /**
     * 同步（扫描）局域网内的海康设备，存储到config.json文件中
     * @param xmlToMap
     * @return
     */
    public static boolean saveOrUpdateDeviceSearch(DeviceSearchInfoDTO xmlToMap) {
        // 判断json文件中是否有deviceLogin数据 && 判断这个数据是否存在
        // 这会有一个并行问题，如果同时打开文件，第一个人修改之后保存为A，第二个人修改之后保存为B，这个内容最终会覆盖A，显示为B内容，目前的业务一般不会出现
        // 判断当前json文件中是否记录改序列号ip
        // 根据设备的ip判断设备是否存在，如果设备已存在，更新此设备；设备不存在，新增设备
        List<DeviceSearchInfo> deviceSearchList = getDeviceSearchInfoList();
        if (deviceSearchList.size() > 0 && ObjectUtil.isNotNull(getDeviceSearchInfoByIp(xmlToMap.getIPv4Address()))) {
            // 说明已存在，需要进行更新
            deviceSearchList.forEach(d -> {
                if (StrUtil.equals(d.getIpv4Address(), xmlToMap.getIPv4Address())) {
                    // BeanUtil.copyProperties(xmlToMap, d);
                    d.setDeviceSearchInfoDTO(xmlToMap);
                }
            });
        } else {
            DeviceSearchInfo deviceSearchInfo = new DeviceSearchInfo();
            deviceSearchInfo.setDeviceSearchInfoDTO(xmlToMap);
            deviceSearchList.add(deviceSearchInfo);
        }
        JSONObject configJson = readConfigJson();
        configJson.put(deviceSearchInfo, deviceSearchList);
        return ConfigJsonUtil.writeConfigJson(configJson.toJSONString());
    }

    public static void searchHikDevice() {
        // 发送
        try (MulticastSocket multicastSocket = new MulticastSocket()) {
            byte[] data = ("<Probe><Uuid>" + IdUtil.randomUUID().toUpperCase() + "</Uuid><Types>inquiry</Types></Probe>").getBytes();
            InetAddress address = InetAddress.getByName("239.255.255.250");
            DatagramPacket packet = new DatagramPacket(data, data.length, address, 37020);
            multicastSocket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
