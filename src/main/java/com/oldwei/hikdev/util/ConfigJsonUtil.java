package com.oldwei.hikdev.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.oldwei.hikdev.entity.device.DeviceLogin;
import com.oldwei.hikdev.entity.device.DeviceSearchInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author oldwei
 * @date 2022/8/12 23:44
 */
public class ConfigJsonUtil {
    static final String configPath = System.getProperty("user.dir") + "/sdk/config/config.json";
    static final String deviceLogin = "deviceLogin";
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

    public static List<DeviceLogin> getDeviceLoginList() {
        JSONObject configJson = readConfigJson();
        List<DeviceLogin> deviceLogin = new ArrayList<>();
        if (configJson.containsKey(ConfigJsonUtil.deviceLogin)) {
            deviceLogin = configJson.getJSONArray(ConfigJsonUtil.deviceLogin).toList(DeviceLogin.class);
        }
        return deviceLogin;
    }

    public static DeviceLogin getDeviceLoginByIp(String ip) {
        List<DeviceLogin> collect = getDeviceLoginList().stream().filter(d -> StrUtil.equals(d.getIp(), ip)).collect(Collectors.toList());
        if (collect.size() > 0) {
            return collect.get(0);
        }
        return null;
    }

    public static boolean saveOrUpdateDeviceLogin(DeviceLogin deviceLogin) {
        // 判断json文件中是否有deviceLogin数据 && 判断这个数据是否存在
        List<DeviceLogin> deviceLoginLoginList = getDeviceLoginList();
        if (deviceLoginLoginList.size() > 0 && ObjectUtil.isNotNull(getDeviceLoginByIp(deviceLogin.getIp()))) {
            deviceLoginLoginList.forEach(d -> {
                if (StrUtil.equals(d.getIp(), deviceLogin.getIp())) {
                    BeanUtil.copyProperties(deviceLogin, d);
                }
            });
        } else {
            deviceLoginLoginList.add(deviceLogin);
        }
        JSONObject configJson = readConfigJson();
        configJson.put(ConfigJsonUtil.deviceLogin, deviceLoginLoginList);
        ConfigJsonUtil.writeConfigJson(configJson.toJSONString());
        return true;
    }

    public static boolean removeDeviceLogin(String ip) {
        // 判断json文件中是否有deviceLogin数据 && 判断这个数据是否存在
        List<DeviceLogin> deviceLoginLoginList = getDeviceLoginList();
        if (deviceLoginLoginList.size() > 0 && ObjectUtil.isNotNull(getDeviceLoginByIp(ip))) {
            deviceLoginLoginList = deviceLoginLoginList.stream().filter(d -> !StrUtil.equals(d.getIp(), ip)).collect(Collectors.toList());
        } else {
            return false;
        }
        JSONObject configJson = readConfigJson();
        configJson.put(ConfigJsonUtil.deviceLogin, deviceLoginLoginList);
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
        List<DeviceSearchInfo> collect = getDeviceSearchInfoList().stream().filter(d -> StrUtil.equals(d.getIPv4Address(), ip)).collect(Collectors.toList());
        if (collect.size() > 0) {
            return collect.get(0);
        }
        return null;
    }

    public static boolean saveOrUpdateDeviceSearch(DeviceSearchInfo xmlToMap) {
        // 判断json文件中是否有deviceLogin数据 && 判断这个数据是否存在
        // 这会有一个并行问题，如果同时打开文件，第一个人修改之后保存为A，第二个人修改之后保存为B，这个内容最终会覆盖A，显示为B内容，目前的业务一般不会出现
        // 判断当前json文件中是否记录改序列号ip
        // 根据设备的ip判断设备是否存在，如果设备已存在，更新此设备；设备不存在，新增设备
        List<DeviceSearchInfo> deviceSearchList = getDeviceSearchInfoList();
        if (deviceSearchList.size() > 0 && ObjectUtil.isNotNull(getDeviceSearchInfoByIp(xmlToMap.getIPv4Address()))) {
            // 说明已存在，需要进行更新
            deviceSearchList.forEach(d -> {
                if (StrUtil.equals(d.getIPv4Address(), xmlToMap.getIPv4Address())) {
                    BeanUtil.copyProperties(xmlToMap, d);
                }
            });
        } else {
            deviceSearchList.add(xmlToMap);
        }
        JSONObject configJson = readConfigJson();
        configJson.put(deviceSearchInfo, deviceSearchList);
        return ConfigJsonUtil.writeConfigJson(configJson.toJSONString());
    }


}
