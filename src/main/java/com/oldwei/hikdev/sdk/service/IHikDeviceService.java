package com.oldwei.hikdev.sdk.service;

import com.alibaba.fastjson.JSONObject;

/**
 * @author oldwei
 * @date 2021-5-19 13:43
 */
public interface IHikDeviceService {
    /**
     * 设备注册
     *
     * @param jsonObject
     * @return
     */
    boolean deviceLogin(JSONObject jsonObject);

    /**
     * 清除设备注册
     * @param ip
     * @return
     */
    boolean clean(String ip);
}
