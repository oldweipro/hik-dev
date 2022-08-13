package com.oldwei.hikdev.controller;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSONObject;
import com.oldwei.hikdev.component.AliyunPlatform;
import com.oldwei.hikdev.entity.StreamAddress;
import com.oldwei.hikdev.constant.DataCachePrefixConstant;
import com.oldwei.hikdev.service.IAccessControlService;
import com.oldwei.hikdev.service.IHikCameraService;
import com.oldwei.hikdev.component.DataCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * @Description
 * @Author oldwei
 * @Date 2019/9/16 19:37
 * @Version 2.0
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {
    private final DataCache dataCache;
    private final IHikCameraService hikCameraService;
    private final AliyunPlatform aliyunPlatform;

    /**
     * 设置内存信息 key value
     *
     * @param key
     * @param msg
     */
    @GetMapping("setMsg")
    public void setMsg(String key, String msg) {
        this.dataCache.set(key, msg);
    }

    /**
     * 通过key获取内存中的数据
     *
     * @param key
     * @return
     */
    @GetMapping("getMsg")
    public String getMsg(String key) {
        return this.dataCache.getString(key);
    }

    /**
     * 使用sdk存储视频录像到本地，每一个小时自动创建新文件
     *
     * @param jsonObject
     * @return
     */
    @PostMapping("saveCameraData")
    public String saveCameraData(@RequestBody JSONObject jsonObject) {
        Integer previewSucValue = this.dataCache.getInteger(DataCachePrefixConstant.HIK_PREVIEW_VIEW + jsonObject.getString("deviceSn"));
        if (null == previewSucValue || previewSucValue == -1) {
            log.error("设备未开启预览");
            return "设备未开启预览";
        }
        this.hikCameraService.saveCameraData(previewSucValue);
        return "启动成功！";
    }

    /**
     * 获取当前内存中缓存的数据，一般存的是当前已注册的设备 各种状态
     *
     * @return
     */
    @PostMapping("getMemory")
    public Map<String, Object> getMemory() {
        log.info("httpUtil");
        return this.dataCache.getData();
    }

    @GetMapping("getPushStream")
    public String getPushStream(String stream) {
        return this.aliyunPlatform.getPushStreamDomain(stream);
    }

    @GetMapping("getPullStream")
    public StreamAddress getPullStream(String stream) {
        return this.aliyunPlatform.getPullStreamDomain(stream);
    }

    @GetMapping("searchDevice")
    public void searchDevice() {
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
