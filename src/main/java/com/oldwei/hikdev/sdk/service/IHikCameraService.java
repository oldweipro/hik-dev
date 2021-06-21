package com.oldwei.hikdev.sdk.service;

import java.io.IOException;

/**
 * @author oldwei
 * @date 2021-5-19 19:32
 */
public interface IHikCameraService {
    /**
     * 开启推流
     */
    void pushStream(Integer userId, String ip, String pushUrl);

    /**
     * sdk录像
     *
     * @param ip
     */
    void saveCameraData(Integer ip);

    /**
     * rtsp推流到rtmp
     *
     * @param rtspUrl
     * @param pushUrl
     * @throws IOException
     */
    void pushRtspToRtmp(String rtspUrl, String pushUrl) throws IOException;
}
