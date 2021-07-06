package com.oldwei.hikdev.sdk.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.oldwei.hikdev.constant.DataCachePrefixConstant;
import com.oldwei.hikdev.sdk.service.FRealDataCallBack_V30;
import com.oldwei.hikdev.sdk.service.IHikDevService;
import com.oldwei.hikdev.sdk.service.IHikPlayCtrlService;
import com.oldwei.hikdev.util.ConvertVideoPacket;
import com.oldwei.hikdev.sdk.service.IHikCameraService;
import com.oldwei.hikdev.sdk.structure.NET_DVR_CLIENTINFO;
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
    public void pushStream(Integer userId, String ip, String pushUrl) {
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
            new ConvertVideoPacket().fromPis(pis).setGrabber().to(pushUrl).go();
            pis.close();
            pos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //======================Javacv推流 pis管道流========================
    }

    @Override
    @Async("asyncServiceExecutor")
    public void saveCameraData(Integer previewSucValue) {
        String randomString = RandomUtil.randomString(32);
        this.hikDevService.NET_DVR_SaveRealData(previewSucValue, randomString + ".mp4");
    }

    @Override
    public void pushRtspToRtmp(String rtspUrl, String pushUrl) throws IOException {
        new ConvertVideoPacket().fromRtsp(rtspUrl).setGrabber().to(pushUrl).go();
    }
}
