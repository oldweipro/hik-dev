package com.oldwei.hikdev.service;

/**
 * @author oldwei
 * @date 2021-5-19 19:32
 */
public interface IHikCameraService {
    /**
     * 开启sdk推流
     *
     * @param userId   用户ID
     * @param deviceSn 开启推流的设备序列号
     * @param pushUrl  流媒体服务器推送地址
     */
    void startPushStream(Integer userId, String deviceSn, String pushUrl);

    /**
     * 退出推流
     *
     * @param deviceSn 退出推流的设备序列号
     * @return
     */
    void existPushStream(String deviceSn);

    /**
     * sdk录像
     *
     * @param ip 需要存储录像的IP地址
     */
    void saveCameraData(Integer ip);

    /**
     * rtsp推流到rtmp
     *
     * @param ip      开启推流的设备IP地址
     * @param rtspUrl rtsp拉流地址
     * @param pushUrl rtmp推流地址
     */
    void pushRtspToRtmp(String ip, String rtspUrl, String pushUrl);
}
