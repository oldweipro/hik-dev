package com.oldwei.hikdev.service;

import com.alibaba.fastjson.JSONObject;
import com.oldwei.hikdev.structure.NET_DVR_ALARMER;
import com.sun.jna.Pointer;

/**
 * @author oldwei
 * @date 2021-5-18 12:01
 */
public interface IHikAlarmDataService {
    /**
     * 回调函数
     *
     * @param lCommand
     * @param pAlarmer
     * @param pAlarmInfo
     * @param dwBufLen
     * @param pUser
     */
    void alarmDataHandle(int lCommand, NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser);

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
