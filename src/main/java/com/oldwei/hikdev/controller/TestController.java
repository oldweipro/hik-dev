package com.oldwei.hikdev.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.oldwei.hikdev.mqtt.MqttConnectClient;
import com.oldwei.hikdev.constant.DataCachePrefixConstant;
import com.oldwei.hikdev.sdk.service.*;
import com.oldwei.hikdev.util.DataCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

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
    private final MqttConnectClient mqttConnectClient;
    private final DataCache dataCache;
    private final IHikCameraService hikCameraService;
    private final IAccessControlService accessControlService;

    @RequestMapping("sendMsg")
    public void sendMsg(String msg) {
        this.mqttConnectClient.publish(msg);
    }

    @GetMapping("setMsg")
    public void setMsg(String key, String msg) {
        this.dataCache.set(key, msg);
    }

    @GetMapping("getMsg")
    public String getMsg(String key) {
        return (String) this.dataCache.get(key);
    }

    @PostMapping("commandMqtt")
    public String commandMqtt(@RequestBody JSONObject command) {
        return this.accessControlService.commandMqtt(command.toJSONString());
    }
    @PostMapping("playCamera")
    public String playCamera(@RequestBody JSONObject jsonObject) {
        String ip = jsonObject.getString("ip");
        String pushUrl = jsonObject.getString("pushUrl");
        Integer userId = (Integer) this.dataCache.get(DataCachePrefixConstant.HIK_REG_USERID_IP + ip);
        if (null == userId || userId < 0) {
            log.error("设备注册异常，可能是没注册，也可能是设备有问题，设备状态userId：{}", userId);
            return "设备注册异常，可能是没注册，也可能是设备有问题，设备状态userId：" + userId;
        }
        Integer getPreviewSucValue = (Integer) this.dataCache.get(DataCachePrefixConstant.HIK_PREVIEW_VIEW_IP);
        if (null != getPreviewSucValue && getPreviewSucValue != -1) {
            log.error("设备已经在预览状态了，请勿重复开启，设备状态userId：{}", userId);
            return "设备已经在预览状态了，请勿重复开启，设备状态userId：" + userId;
        }
        this.hikCameraService.pushStream(userId, ip, pushUrl);
        return "推流成功";
    }

    @PostMapping("saveCameraData")
    public String saveCameraData(@RequestBody JSONObject jsonObject) {
        Integer previewSucValue = (Integer) this.dataCache.get(DataCachePrefixConstant.HIK_PREVIEW_VIEW_IP + jsonObject.getString("ip"));
        if (null == previewSucValue || previewSucValue == -1) {
            log.error("设备未开启预览");
            return "设备未开启预览";
        }
        this.hikCameraService.saveCameraData(previewSucValue);
        return "启动成功！";
    }

    @PostMapping("pushRtspToRtmp")
    public String pushRtspToRtmp(@RequestBody JSONObject jsonObject) throws IOException {
        String rtspUrl = jsonObject.getString("rtspUrl");
        String pushUrl = jsonObject.getString("pushUrl");
        if (StrUtil.isBlank(rtspUrl) || StrUtil.isBlank(pushUrl)) {
            return "缺少参数 rtspUrl 或 pushUrl";
        }
        this.hikCameraService.pushRtspToRtmp(rtspUrl, pushUrl);
        return "推流成功";
    }

    @PostMapping("getMemory")
    public Map<String, Object> getMemory() {
        return this.dataCache.getData();
    }
}
