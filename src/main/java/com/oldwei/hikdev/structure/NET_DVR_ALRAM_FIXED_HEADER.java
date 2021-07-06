package com.oldwei.hikdev.structure;

import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 12:15
 */
public class NET_DVR_ALRAM_FIXED_HEADER extends Structure {
    public int dwAlarmType;
    public NET_DVR_TIME_EX struAlarmTime = new NET_DVR_TIME_EX();
    public uStruAlarm ustruAlarm = new uStruAlarm();
}
