package com.oldwei.hikdev.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.oldwei.hikdev.component.ConvertVideoPacket;
import com.oldwei.hikdev.constant.DataCachePrefixConstant;
import com.oldwei.hikdev.service.FRealDataCallBack_V30;
import com.oldwei.hikdev.service.IHikDevService;
import com.oldwei.hikdev.service.IHikPlayCtrlService;
import com.oldwei.hikdev.service.IHikCameraService;
import com.oldwei.hikdev.structure.NET_DVR_CLIENTINFO;
import com.oldwei.hikdev.component.DataCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * @author oldwei
 * @date 2021-5-19 19:33
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HikCameraServiceImpl implements IHikCameraService {
    private final IHikDevService hikDevService;
    private final DataCache dataCache;
    private final IHikPlayCtrlService hikPlayCtrlService;
    private final ConvertVideoPacket convertVideoPacket;

    @Override
    @Async("asyncServiceExecutor")
    public void startPushStream(Integer userId, String deviceSn, String pushUrl) {
        //======================管道流代码========================
        PipedInputStream pis = new PipedInputStream(5120);
        PipedOutputStream pos = new PipedOutputStream();
        FRealDataCallBack_V30 hikCameraRealDataCallBack = new HikCameraRealDataCallBackImpl(this.hikPlayCtrlService, pos);
        try {
            pos.connect(pis);
        } catch (IOException e) {
            log.error("管道流连接:{}", e.getMessage());
        }
        //======================管道流代码========================
        //======================开启设备预览========================
        NET_DVR_CLIENTINFO strClientInfo = new NET_DVR_CLIENTINFO();
        strClientInfo.lChannel = 1;
        strClientInfo.hPlayWnd = null;
        int previewSucValue = this.hikDevService.NET_DVR_RealPlay_V30(userId, strClientInfo, hikCameraRealDataCallBack, null, true);
        //预览失败时:
        if (previewSucValue == -1) {
            log.info(deviceSn + "预览失败，previewSucValue的值：{}", previewSucValue);
        } else {
            log.info(deviceSn + "预览成功，previewSucValue：{}", previewSucValue);
        }
        // TODO 需要将previewSucValue存储到json文件
        this.dataCache.set(DataCachePrefixConstant.HIK_PREVIEW_VIEW + deviceSn, previewSucValue);
        //======================开启设备预览========================
        //======================Javacv推流 pis管道流========================
        try {
            this.convertVideoPacket.fromPis(pis, pushUrl, deviceSn);
            pis.close();
            pos.close();
        } catch (IOException e) {
            log.error("推流:{}", e.getMessage());
        }
        //======================Javacv推流 pis管道流========================
    }

    @Override
    public void existPushStream(String deviceSn) {
        //获取sdk预览状态
        Integer previewView = this.dataCache.getInteger(DataCachePrefixConstant.HIK_PREVIEW_VIEW + deviceSn);
        if (null != previewView && previewView != -1) {
            if (this.hikDevService.NET_DVR_StopRealPlay(previewView)) {
                log.info("退出SDK预览成功！");
            } else {
                log.info("退出SDK预览失败！");
            }
        }
        //将推流状态设置为0,在推流循环里会判断状态
        this.dataCache.removeKey(DataCachePrefixConstant.HIK_PUSH_STATUS + deviceSn);
        //移除缓存中的拉流地址
        this.dataCache.removeKey(DataCachePrefixConstant.HIK_PREVIEW_VIEW + deviceSn);
        this.dataCache.removeKey(DataCachePrefixConstant.HIK_PUSH_PULL_STREAM_ADDRESS + deviceSn);
    }

    @Override
    @Async("asyncServiceExecutor")
    public void saveCameraData(Integer previewSucValue) {
        String randomString = RandomUtil.randomString(32);
        this.hikDevService.NET_DVR_SaveRealData(previewSucValue, randomString + ".mp4");
    }

    @Override
    @Async("asyncServiceExecutor")
    public void pushRtspToRtmp(String ip, String rtspUrl, String pushUrl) {
        try {
            this.convertVideoPacket.fromRtsp(rtspUrl, pushUrl, ip);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void openPreview(Integer loginId, String ipv4Address) {
        PipedOutputStream pos = new PipedOutputStream();
        FRealDataCallBack_V30 hikCameraRealDataCallBack = new HikCameraRealDataCallBackImpl(this.hikPlayCtrlService, pos);
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
        // TODO 需要将previewSucValue存储到json文件
        this.dataCache.set(DataCachePrefixConstant.HIK_PREVIEW_VIEW + ipv4Address, previewSucValue);
        //======================开启设备预览========================
    }
}
