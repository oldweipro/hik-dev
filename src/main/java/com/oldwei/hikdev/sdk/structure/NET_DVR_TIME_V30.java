package com.oldwei.hikdev.sdk.structure;

import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 13:18
 */
public class NET_DVR_TIME_V30 extends Structure {
    public short wYear;
    public byte byMonth;
    public byte byDay;
    public byte byHour;
    public byte byMinute;
    public byte bySecond;
    public byte byRes;
    public short wMilliSec;
    public byte[] byRes1 = new byte[2];
}
