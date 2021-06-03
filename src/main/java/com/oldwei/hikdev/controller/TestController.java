package com.oldwei.hikdev.controller;

import com.alibaba.fastjson.JSONObject;
import com.oldwei.hikdev.mqtt.MqttConnectClient;
import com.oldwei.hikdev.sdk.constant.RedisPrefixConstant;
import com.oldwei.hikdev.sdk.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;

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
    private final RedisTemplate<String, Serializable> redisTemplate;
    private final IHikCameraService hikCameraService;
    private final IAccessControlService accessControlService;

    @RequestMapping("sendMsg")
    public void sendMsg(String msg) {
        this.mqttConnectClient.publish(msg);
    }

    @GetMapping("setMsg")
    public void setMsg(String key, String msg) {
        this.redisTemplate.opsForValue().set(key, msg);
    }

    @GetMapping("getMsg")
    public String getMsg(String key) {
        return (String) this.redisTemplate.opsForValue().get(key);
    }

    @PostMapping("commandMqtt")
    public String commandMqtt(@RequestBody JSONObject command) {
        return this.accessControlService.commandMqtt(command.toJSONString());
    }
    @PostMapping("playCamera")
    public String playCamera(@RequestBody JSONObject jsonObject) {
        String ip = jsonObject.getString("ip");
        String pushUrl = jsonObject.getString("pushUrl");
        Integer userId = (Integer) this.redisTemplate.opsForValue().get(RedisPrefixConstant.HIK_REG_USERID_IP + ip);
        if (null == userId || userId < 0) {
            log.error("设备注册异常，可能是没注册，也可能是设备有问题，设备状态userId：{}", userId);
            return "设备注册异常，可能是没注册，也可能是设备有问题，设备状态userId：" + userId;
        }
        Integer getPreviewSucValue = (Integer) this.redisTemplate.opsForValue().get(RedisPrefixConstant.HIK_PREVIEW_VIEW_IP);
        if (null != getPreviewSucValue && getPreviewSucValue != -1) {
            log.error("设备已经在预览状态了，请勿重复开启，设备状态userId：{}", userId);
            return "设备已经在预览状态了，请勿重复开启，设备状态userId：" + userId;
        }
        this.hikCameraService.pushStream(userId, ip, pushUrl);
        return "推流成功";
    }

    @PostMapping("saveCameraData")
    public String saveCameraData(@RequestBody JSONObject jsonObject) {
        Integer previewSucValue = (Integer) this.redisTemplate.opsForValue().get(RedisPrefixConstant.HIK_PREVIEW_VIEW_IP + jsonObject.getString("ip"));
        if (null == previewSucValue || previewSucValue == -1) {
            log.error("设备未开启预览");
            return "设备未开启预览";
        }
        this.hikCameraService.saveCameraData(previewSucValue);
        return "启动成功！";
    }
}
