package com.oldwei.hikdev.service;

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
}
