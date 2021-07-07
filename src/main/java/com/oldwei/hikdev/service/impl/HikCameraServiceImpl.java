package com.oldwei.hikdev.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.oldwei.hikdev.constant.DataCachePrefixConstant;
import com.oldwei.hikdev.service.FRealDataCallBack_V30;
import com.oldwei.hikdev.service.IHikDevService;
import com.oldwei.hikdev.service.IHikPlayCtrlService;
import com.oldwei.hikdev.service.IHikCameraService;
import com.oldwei.hikdev.util.ConvertVideoPacket;
import com.oldwei.hikdev.structure.NET_DVR_CLIENTINFO;
import com.oldwei.hikdev.util.DataCache;
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

    @Override
    @Async("asyncServiceExecutor")
    public void startPushStream(Integer userId, String ip, String pushUrl) {
        //======================管道流代码========================
        PipedInputStream pis = new PipedInputStream(5120);
        PipedOutputStream pos = new PipedOutputStream();
        FRealDataCallBack_V30 videoRealDataCallBack = new HikCameraRealDataCallBackImpl(this.hikPlayCtrlService, pos);
        try {
            pos.connect(pis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //======================管道流代码========================
        //======================开启设备预览========================
        NET_DVR_CLIENTINFO strClientInfo = new NET_DVR_CLIENTINFO();
        strClientInfo.lChannel = 1;
        strClientInfo.hPlayWnd = null;
        int previewSucValue = this.hikDevService.NET_DVR_RealPlay_V30(userId, strClientInfo, videoRealDataCallBack, null, true);
        //预览失败时:
        if (previewSucValue == -1) {
            log.info(ip + "预览失败，previewSucValue的值：{}", previewSucValue);
        } else {
            log.info(ip + "预览成功，previewSucValue：{}", previewSucValue);
        }
        this.dataCache.set(DataCachePrefixConstant.HIK_PREVIEW_VIEW_IP + ip, previewSucValue);
        //======================开启设备预览========================
        //======================Javacv推流 pis管道流========================
        try {
            new ConvertVideoPacket(this.dataCache).fromPis(pis).setGrabber().to(pushUrl).go(ip);
            pis.close();
            pos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //======================Javacv推流 pis管道流========================
    }

    @Override
    public boolean existPushStream(String ip) {
        //将推流状态设置为0,在推流循环里会判断状态
        this.dataCache.set(DataCachePrefixConstant.HIK_PUSH_STATUS_IP + ip, 0);
        Integer previewView = this.dataCache.getInteger(DataCachePrefixConstant.HIK_PREVIEW_VIEW_IP + ip);
        if (null != previewView && previewView != -1) {
            boolean b = this.hikDevService.NET_DVR_StopRealPlay(previewView);
            if (b) {
                log.info("退出预览成功！");
            } else {
                log.info("退出预览失败！");
            }
            return b;
        } else {
            return true;
        }
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
            new ConvertVideoPacket(this.dataCache).fromRtsp(rtspUrl).setGrabber().to(pushUrl).go(ip);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
