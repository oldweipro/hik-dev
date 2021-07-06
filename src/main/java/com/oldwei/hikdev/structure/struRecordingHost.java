package com.oldwei.hikdev.structure;

import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 12:17
 */
public class struRecordingHost extends Structure {
    public byte bySubAlarmType;
    public byte[] byRes1 = new byte[3];
    public NET_DVR_TIME_EX struRecordEndTime = new NET_DVR_TIME_EX();
    public byte[] byRes = new byte[116];
}
