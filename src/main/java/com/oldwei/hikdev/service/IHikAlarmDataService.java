package com.oldwei.hikdev.service;

import com.alibaba.fastjson.JSONObject;
import com.oldwei.hikdev.entity.HikDevResponse;

/**
 * @author oldwei
 * @date 2021-5-18 12:01
 */
public interface IHikAlarmDataService {

    /**
     * 设置报警 设备布防
     *
     * @param deviceSn 设备序列号
     * @return
     */
    HikDevResponse setupAlarmChan(String deviceSn);

    /**
     * 报警撤防 设备撤防
     *
     * @param deviceSn 设备序列号
     * @return
     */
    HikDevResponse closeAlarmChan(String deviceSn);

    /**
     * 启动监听
     *
     * @param jsonObject
     * @return
     */
    JSONObject startAlarmListen(JSONObject jsonObject);

    /**
     * 停止监听
     *
     * @param jsonObject
     * @return
     */
    JSONObject stopAlarmListen(JSONObject jsonObject);
}
