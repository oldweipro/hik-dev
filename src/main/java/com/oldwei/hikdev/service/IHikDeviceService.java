package com.oldwei.hikdev.service;

import com.oldwei.hikdev.entity.Device;

/**
 * @author oldwei
 * @date 2021-5-19 13:43
 */
public interface IHikDeviceService {
    /**
     * 设备注册
     *
     * @param device
     * @return
     */
    boolean login(Device device);

    /**
     * 清除设备注册
     * @param deviceSn
     * @return
     */
    boolean clean(String deviceSn);
}
