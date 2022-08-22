package com.oldwei.hikdev.structure;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 12:13
 */
public class NET_DVR_ALARMINFO_V40 extends Structure {
    public NET_DVR_ALRAM_FIXED_HEADER struAlarmFixedHeader = new NET_DVR_ALRAM_FIXED_HEADER();
    public Pointer pAlarmData;
}
