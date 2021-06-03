package com.oldwei.hikdev.sdk.structure;

import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 13:42
 */
public class NET_DVR_SYSTEM_TIME extends Structure {
    public short wYear;           //年
    public short wMonth;          //月
    public short wDay;            //日
    public short wHour;           //时
    public short wMinute;      //分
    public short wSecond;      //秒
    public short wMilliSec;    //毫秒
    public byte[] byRes = new byte[2];
}
