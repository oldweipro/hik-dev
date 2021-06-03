package com.oldwei.hikdev.sdk.structure;

import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 12:14
 */
public class struIOAlarm extends Structure {
    public int dwAlarmInputNo;
    public int dwTrigerAlarmOutNum;
    public int dwTrigerRecordChanNum;
}
