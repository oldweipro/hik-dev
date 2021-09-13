package com.oldwei.hikdev.service;

import com.alibaba.fastjson.JSONObject;

/**
 * @author oldwei
 * @date 2021-5-18 12:01
 */
public interface IHikAlarmDataService {

    /**
     * 设置报警 设备布防
     *
     * @param ip 设备IP
     * @return
     */
    JSONObject setupAlarmChan(String ip);

    /**
     * 报警撤防 设备撤防
     *
     * @param ip 设备IP
     * @return
     */
    JSONObject closeAlarmChan(String ip);

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
