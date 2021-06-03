package com.oldwei.hikdev.sdk.structure;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 13:43
 */
public class NET_DVR_ALARM_ISAPI_INFO extends Structure {
    public Pointer pAlarmData;           // 报警数据（参见下表）
    public int dwAlarmDataLen;   // 报警数据长度
    public byte byDataType;        // 0-invalid,1-xml,2-json
    public byte byPicturesNumber;  // 图片数量
    public byte[] byRes = new byte[2];
    public Pointer pPicPackData;         // 图片变长部分
    //（byPicturesNumber个{NET_DVR_ALARM_ISAPI_PICDATA}；）
    public byte[] byRes1 = new byte[32];
}
