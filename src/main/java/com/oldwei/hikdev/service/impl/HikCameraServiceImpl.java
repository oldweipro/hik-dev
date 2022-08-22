package com.oldwei.hikdev.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.oldwei.hikdev.entity.config.DeviceHandleDTO;
import com.oldwei.hikdev.service.FRealDataCallBack_V30;
import com.oldwei.hikdev.service.IHikDevService;
import com.oldwei.hikdev.service.IHikPlayCtrlService;
import com.oldwei.hikdev.service.IHikCameraService;
import com.oldwei.hikdev.structure.NET_DVR_CLIENTINFO;
import com.oldwei.hikdev.util.ConfigJsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author oldwei
 * @date 2021-5-19 19:33
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HikCameraServiceImpl implements IHikCameraService {
    private final IHikDevService hikDevService;
    private final IHikPlayCtrlService hikPlayCtrlService;

    @Override
    public void saveCameraData(Integer previewSucValue) {
        String randomString = RandomUtil.randomString(32);
        this.hikDevService.NET_DVR_SaveRealData(previewSucValue, randomString + ".mp4");
    }

    @Override
    public void openPreview(Integer loginId, String ipv4Address) {
        FRealDataCallBack_V30 hikCameraRealDataCallBack = new HikCameraRealDataCallBackImpl(this.hikPlayCtrlService);
        //======================开启设备预览========================
        NET_DVR_CLIENTINFO strClientInfo = new NET_DVR_CLIENTINFO();
        strClientInfo.lChannel = 1;
        strClientInfo.hPlayWnd = null;
        int previewSucValue = this.hikDevService.NET_DVR_RealPlay_V30(loginId, strClientInfo, hikCameraRealDataCallBack, null, true);
        //预览失败时:
        if (previewSucValue == -1) {
            log.info(ipv4Address + "预览失败，previewSucValue的值：{}", previewSucValue);
        } else {
            log.info(ipv4Address + "预览成功，previewSucValue：{}", previewSucValue);
        }
        // TEST 需要将previewSucValue存储到json文件
        DeviceHandleDTO deviceHandleDTO = new DeviceHandleDTO();
        deviceHandleDTO.setPreviewHandleId(previewSucValue);
        deviceHandleDTO.setIpv4Address(ipv4Address);
        ConfigJsonUtil.updateDeviceHandle(deviceHandleDTO);
        //======================开启设备预览========================
    }
}
