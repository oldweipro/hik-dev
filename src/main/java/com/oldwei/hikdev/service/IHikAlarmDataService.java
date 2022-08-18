package com.oldwei.hikdev.service;

import com.alibaba.fastjson2.JSONObject;
import com.oldwei.hikdev.entity.HikDevResponse;

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
    HikDevResponse setupAlarmChan(String ip);

    /**
     * 报警撤防 设备撤防
     *
     * @param ip 设备ip
     * @return
     */
    HikDevResponse closeAlarmChan(String ip);

    /**
     * 启动监听
     * 业务逻辑需要重写
     * 即将在未来版本删除
     *
     * @param jsonObject
     * @return
     */
    @Deprecated
    JSONObject startAlarmListen(JSONObject jsonObject);

    /**
     * 停止监听
     * 业务逻辑需要重写
     * 即将在未来版本删除
     *
     * @param jsonObject
     * @return
     */
    @Deprecated
    JSONObject stopAlarmListen(JSONObject jsonObject);
}
