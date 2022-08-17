package com.oldwei.hikdev.service;

import com.oldwei.hikdev.entity.config.DeviceLoginDTO;
import com.oldwei.hikdev.entity.config.DeviceSearchInfo;
import com.oldwei.hikdev.entity.config.DeviceSearchInfoVO;

import java.util.List;

/**
 * @author oldwei
 * @date 2021-5-19 13:43
 */
public interface IHikDeviceService {
    /**
     * 设备注册
     *
     * @param deviceLogin
     * @return
     */
    boolean login(DeviceLoginDTO deviceLogin);

    /**
     * 清除设备注册
     *
     * @param ip
     * @return
     */
    boolean clean(String ip);

    /**
     * 获取设备列表
     *
     * @param deviceSearchInfoVo
     * @return
     */
    List<DeviceSearchInfoVO> getDeviceList(DeviceSearchInfoVO deviceSearchInfoVo);

    /**
     * 获取登陆状态
     * @param ip
     * @return
     */
    DeviceSearchInfo loginStatus(String ip);
}
