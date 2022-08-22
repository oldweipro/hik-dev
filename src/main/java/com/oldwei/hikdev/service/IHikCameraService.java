package com.oldwei.hikdev.service;

/**
 * @author oldwei
 * @date 2021-5-19 19:32
 */
public interface IHikCameraService {

    /**
     * sdk录像
     *
     * @param ip 需要存储录像的IP地址
     */
    void saveCameraData(Integer ip);

    /**
     * 开启预览
     * @param loginId 设备注册id
     * @param ipv4Address 设备IP
     */
    void openPreview(Integer loginId, String ipv4Address);
}
