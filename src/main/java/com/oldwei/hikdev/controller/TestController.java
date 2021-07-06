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

    /**
     * mqtt发送消息
     *
     * @param msg
     */
    @RequestMapping("sendMsg")
    public void sendMsg(String msg) {
        this.mqttConnectClient.publish(msg);
    }

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
     * 接收MQTT传输的指令进行相关操作
     * TODO 将来会进行归档整理，详细指令进入AccessControlServiceImpl.class查看
     *
     * @param command
     * @return
     */
    @PostMapping("commandMqtt")
    public String commandMqtt(@RequestBody JSONObject command) {
        return this.accessControlService.commandMqtt(command.toJSONString());
    }

    /**
     * 设备sdk打开预览，获取到流数据，进行推流，需要填写推送地址例如：rtmp://ip:port/live/stream
     * 自行部署流媒体服务器
     * TODO 有内存溢出问题，随时间无限增大，目前推荐使用rtspToRtmp进行推流
     *
     * @param jsonObject
     * @return
     */
    @PostMapping("startPushStream")
    public String startPushStream(@RequestBody JSONObject jsonObject) {
        String ip = jsonObject.getString("ip");
        String pushUrl = jsonObject.getString("pushUrl");
        Integer userId = this.dataCache.getInteger(DataCachePrefixConstant.HIK_REG_USERID_IP + ip);
        if (null == userId || userId < 0) {
            log.error("设备注册异常，可能是没注册，也可能是设备有问题，设备状态userId：{}", userId);
            return "设备注册异常，可能是没注册，也可能是设备有问题，设备状态userId：" + userId;
        }
        Integer getPreviewSucValue = this.dataCache.getInteger(DataCachePrefixConstant.HIK_PREVIEW_VIEW_IP);
        if (null != getPreviewSucValue && getPreviewSucValue != -1) {
            log.error("设备已经在预览状态了，请勿重复开启，设备状态userId：{}", userId);
            return "设备已经在预览状态了，请勿重复开启，设备状态userId：" + userId;
        }
        this.hikCameraService.startPushStream(userId, ip, pushUrl);
        return "推流成功";
    }

    /**
     * 退出推流
     *
     * @param jsonObject 退出推流的设备IP
     */
    @PostMapping("existPushStream")
    public boolean existPushStream(@RequestBody JSONObject jsonObject) {
        String ip = jsonObject.getString("ip");
        return this.hikCameraService.existPushStream(ip);
    }

    /**
     * 使用sdk存储视频录像到本地，每一个小时自动创建新文件
     *
     * @param jsonObject
     * @return
     */
    @PostMapping("saveCameraData")
    public String saveCameraData(@RequestBody JSONObject jsonObject) {
        Integer previewSucValue = this.dataCache.getInteger(DataCachePrefixConstant.HIK_PREVIEW_VIEW_IP + jsonObject.getString("ip"));
        if (null == previewSucValue || previewSucValue == -1) {
            log.error("设备未开启预览");
            return "设备未开启预览";
        }
        this.hikCameraService.saveCameraData(previewSucValue);
        return "启动成功！";
    }

    /**
     * 使用rtsp推流 海康rtsp取流地址参考：https://www.jianshu.com/p/8efcea89b11f
     *
     * @param jsonObject rtspUrl拉流地址：rtsp://ip:port/live/stream
     *                   pushUrl推流地址：rtmp://ip:port/live/stream
     * @return
     * @throws IOException
     */
    @PostMapping("pushRtspToRtmp")
    public String pushRtspToRtmp(@RequestBody JSONObject jsonObject) {
        String ip = jsonObject.getString("ip");
        String rtspUrl = jsonObject.getString("rtspUrl");
        String pushUrl = jsonObject.getString("pushUrl");
        if (StrUtil.isBlank(ip) || StrUtil.isBlank(rtspUrl) || StrUtil.isBlank(pushUrl)) {
            return "缺少参数: ip rtspUrl 或 pushUrl";
        }
        this.hikCameraService.pushRtspToRtmp(ip, rtspUrl, pushUrl);
        return "推流成功";
    }

    /**
     * 获取当前内存中缓存的数据，一般存的是当前已注册的设备 各种状态
     *
     * @return
     */
    @PostMapping("getMemory")
    public Map<String, Object> getMemory() {
        return this.dataCache.getData();
    }
}
