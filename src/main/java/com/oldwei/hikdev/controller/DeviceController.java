package com.oldwei.hikdev.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.oldwei.hikdev.annotation.CheckDeviceLogin;
import com.oldwei.hikdev.component.AliyunPlatform;
import com.oldwei.hikdev.constant.DataCachePrefixConstant;
import com.oldwei.hikdev.entity.config.DeviceLoginDTO;
import com.oldwei.hikdev.entity.HikDevResponse;
import com.oldwei.hikdev.entity.StreamAddress;
import com.oldwei.hikdev.entity.config.DeviceSearchInfo;
import com.oldwei.hikdev.entity.config.DeviceSearchInfoVO;
import com.oldwei.hikdev.entity.param.DeviceSn;
import com.oldwei.hikdev.service.IHikAlarmDataService;
import com.oldwei.hikdev.service.IHikCameraService;
import com.oldwei.hikdev.service.IHikDeviceService;
import com.oldwei.hikdev.component.DataCache;
import com.oldwei.hikdev.util.ConfigJsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author oldwei
 * @date 2021-7-7 14:37
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("api/device")
public class DeviceController {

    private final IHikDeviceService hikDeviceService;

    private final DataCache dataCache;

    private final IHikCameraService hikCameraService;

    private final IHikAlarmDataService hikAlarmDataService;

    private final AliyunPlatform aliyunPlatform;


    /**
     * 设备注册登录
     *
     * @param deviceLogin 设备基本信息IP、username、password、port、设备序列号deviceSn
     * @return 登录结果 true/false
     */
    @PostMapping("login")
    public HikDevResponse login(@Valid @RequestBody DeviceLoginDTO deviceLogin) {
        return this.hikDeviceService.login(deviceLogin) ? new HikDevResponse().ok() : new HikDevResponse().err();
    }

    /**
     * 设备注册登录状态
     *
     * @param ip 设备ip
     * @return 登录结果 true/false
     */
    @GetMapping("loginStatus")
    public HikDevResponse loginStatus(String ip) {
        DeviceSearchInfo deviceLogin = this.hikDeviceService.loginStatus(ip);
        return new HikDevResponse().ok().data(deviceLogin);
    }

    /**
     * 设备注销退出
     *
     * @param ip 设备ip
     * @return 注销结果 true/false
     */
    @CheckDeviceLogin
    @PostMapping("clean")
    public HikDevResponse clean(@RequestBody String ip) {
        return this.hikDeviceService.clean(ip) ? new HikDevResponse().ok() : new HikDevResponse().err();
    }

    @GetMapping("getDeviceList")
    public HikDevResponse getDeviceList(DeviceSearchInfoVO deviceSearchInfoVo) {
        // TODO 获取所有的设备，及登录状态
        return new HikDevResponse().ok().data(this.hikDeviceService.getDeviceList(deviceSearchInfoVo));
    }

    /**
     * 主动同步（扫描）局域网设备
     */
    @GetMapping("searchHikDevice")
    public HikDevResponse searchHikDevice() {
        ConfigJsonUtil.searchHikDevice();
        return new HikDevResponse().ok();
    }

    /**
     * 设备布防
     *
     * @param deviceSn 设备序列号deviceSn
     */
    @CheckDeviceLogin
    @PostMapping("setupAlarm")
    public HikDevResponse setupAlarm(@RequestBody DeviceSn deviceSn) {
        return this.hikAlarmDataService.setupAlarmChan(deviceSn.getDeviceSn());
    }

    /**
     * 设备布防状态
     *
     * @param deviceSn 设备序列号
     * @return 登录结果 true/false
     */
    @CheckDeviceLogin
    @GetMapping("alarmStatus")
    public HikDevResponse alarmStatus(String deviceSn) {
        return ObjectUtil.isNotEmpty(this.dataCache.get(DataCachePrefixConstant.HIK_ALARM_HANDLE + deviceSn)) ? new HikDevResponse().ok("已布防") : new HikDevResponse().err("未布防");
    }

    /**
     * 设备撤防
     *
     * @param deviceSn 设备序列号deviceSn
     */
    @CheckDeviceLogin
    @PostMapping("closeAlarm")
    public HikDevResponse closeAlarm(@RequestBody DeviceSn deviceSn) {
        return this.hikAlarmDataService.closeAlarmChan(deviceSn.getDeviceSn());
    }














    /**
     * 设备推流状态
     *
     * @param deviceLogin 设备基本信息IP、username、password、port
     * @return 登录结果 true/false
     */
    @GetMapping("devicePushStatus")
    public String devicePushStatus(DeviceLoginDTO deviceLogin) {
        return ObjectUtil.isNotEmpty(this.dataCache.get(DataCachePrefixConstant.HIK_PUSH_STATUS + deviceLogin.getIpv4Address())) ? "推流中" : "未推流";
    }

    /**
     * 设备sdk打开预览，获取到流数据，进行推流，需要填写推送地址例如：rtmp://ip:port/live/stream
     * 自行部署流媒体服务器
     *
     * @param ip 设备IP
     * @return 拉流地址
     */
    @PostMapping("startPushStream/{ip}")
    public JSONObject startPushStream(@PathVariable String ip) {
        JSONObject result = new JSONObject();
        Integer userId = this.dataCache.getInteger(DataCachePrefixConstant.HIK_REG_USERID + ip);
        Integer getPreviewSucValue = this.dataCache.getInteger(DataCachePrefixConstant.HIK_PREVIEW_VIEW + ip);
        if (null != getPreviewSucValue && getPreviewSucValue != -1) {
            result.put("code", -1);
            result.put("msg", "设备已经在预览状态了，请勿重复开启，设备状态userId：" + userId);
            return result;
        }
        String stream = RandomUtil.randomString(32);
        String pushStreamDomain = this.aliyunPlatform.getPushStreamDomain(stream);
        StreamAddress pullStreamDomain = this.aliyunPlatform.getPullStreamDomain(stream);
        this.hikCameraService.startPushStream(userId, ip, pushStreamDomain);
        this.dataCache.set(DataCachePrefixConstant.HIK_PUSH_PULL_STREAM_ADDRESS + ip, pullStreamDomain);
        result.put("code", 0);
        result.put("data", pullStreamDomain);
        return result;
    }

    /**
     * 根据设备IP获取流地址
     *
     * @param ip 设备IP
     * @return 流地址
     */
    @GetMapping("streamList/{ip}")
    public JSONObject getStreamList(@PathVariable String ip) {
        JSONObject result = new JSONObject();
        StreamAddress streamAddress = (StreamAddress) this.dataCache.get(DataCachePrefixConstant.HIK_PUSH_PULL_STREAM_ADDRESS + ip);
        result.put("code", 0);
        result.put("data", streamAddress);
        return result;
    }

    /**
     * 获取所有设备的流地址
     *
     * @return 所有设备的流地址
     */
    @GetMapping("streamList")
    public JSONObject streamList() {
        JSONObject result = new JSONObject();
        Map<String, Object> streamAddress = this.dataCache.getData().entrySet().stream()
                .filter(map -> map.getKey().contains(DataCachePrefixConstant.HIK_PUSH_PULL_STREAM_ADDRESS))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        List<Map<String, Object>> mapList = new ArrayList<>();
        streamAddress.forEach((key, value) -> {
            Map<String, Object> map = new HashMap<>(2);
            map.put("deviceIp", key);
            map.put("streamAddress", value);
            mapList.add(map);
        });
        result.put("code", 0);
        result.put("data", mapList);
        return result;
    }

    /**
     * 使用rtsp推流 海康rtsp取流地址参考：https://www.jianshu.com/p/8efcea89b11f
     *
     * @param jsonObject rtspUrl拉流地址：rtsp://ip:port/live/stream
     * @return
     * @throws IOException
     */
    @PostMapping("pushRtspToRtmp")
    public JSONObject pushRtspToRtmp(@RequestBody JSONObject jsonObject) {
        //TODO 只需要一个IP地址，推送至指定流媒体服务器，在配置文件中配置推送的流媒体服务器
        JSONObject result = new JSONObject();
        String ip = jsonObject.getString("ip");
        String rtspUrl = jsonObject.getString("rtspUrl");
        String pushUrl = jsonObject.getString("pushUrl");
        if (StrUtil.isBlank(ip) || StrUtil.isBlank(rtspUrl)) {
            result.put("code", -1);
            result.put("msg", "缺少参数: ip 或 rtspUrl");
            return result;
        }
        this.hikCameraService.pushRtspToRtmp(ip, rtspUrl, pushUrl);
        result.put("code", 0);
        result.put("data", pushUrl);
        return result;
    }

    @GetMapping("wurenji")
    public JSONObject wurenji(String name) {
        String stream = RandomUtil.randomString(32);
        String pushStreamDomain = this.aliyunPlatform.getPushStreamDomain(stream);
        StreamAddress pullStreamDomain = this.aliyunPlatform.getPullStreamDomain(stream);
        this.dataCache.set(DataCachePrefixConstant.HIK_PUSH_PULL_STREAM_ADDRESS + name, pullStreamDomain);
        JSONObject result = new JSONObject();
        result.put("code", 0);
        result.put("data", pullStreamDomain);
        result.put("msg", pushStreamDomain);
        return result;
    }

    @GetMapping("shanchuwurenji")
    public JSONObject shanchuwurenji(String name) {
        this.dataCache.removeKey(DataCachePrefixConstant.HIK_PUSH_PULL_STREAM_ADDRESS + name);
        JSONObject result = new JSONObject();
        result.put("code", 0);
        return result;
    }

    /**
     * 退出推流
     *
     * @param deviceLogin 退出推流的设备IP
     */
    @PostMapping("existPushStream")
    public JSONObject existPushStream(@RequestBody DeviceLoginDTO deviceLogin) {
        this.hikCameraService.existPushStream(deviceLogin.getIpv4Address());
        JSONObject result = new JSONObject();
        result.put("code", 0);
        result.put("msg", "已退出推流:" + deviceLogin.getIpv4Address());
        return result;
    }
}
