package com.oldwei.hikdev.controller;

import com.oldwei.hikdev.component.AliyunPlatform;
import com.oldwei.hikdev.entity.StreamAddress;
import com.oldwei.hikdev.service.IHikCameraService;
import com.oldwei.hikdev.util.ConfigJsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
    private final IHikCameraService hikCameraService;
    private final AliyunPlatform aliyunPlatform;

    /**
     * 使用sdk存储视频录像到本地，每一个小时自动创建新文件
     *
     * @param ip 设备IP
     * @return
     */
    @PostMapping("saveCameraData")
    public String saveCameraData(String ip) {
        Integer previewSucValue = ConfigJsonUtil.getDeviceSearchInfoByIp(ip).getPreviewHandle();
        if (null == previewSucValue || previewSucValue == -1) {
            log.error("设备未开启预览");
            return "设备未开启预览";
        }
        this.hikCameraService.saveCameraData(previewSucValue);
        return "启动成功！";
    }

    @GetMapping("getPushStream")
    public String getPushStream(String stream) {
        return this.aliyunPlatform.getPushStreamDomain(stream);
    }

    @GetMapping("getPullStream")
    public StreamAddress getPullStream(String stream) {
        return this.aliyunPlatform.getPullStreamDomain(stream);
    }
}
