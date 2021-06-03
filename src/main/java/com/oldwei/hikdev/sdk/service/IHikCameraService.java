package com.oldwei.hikdev.sdk.service;

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
}
