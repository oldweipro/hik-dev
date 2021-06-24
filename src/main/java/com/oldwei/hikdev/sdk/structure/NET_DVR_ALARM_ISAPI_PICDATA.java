package com.oldwei.hikdev.sdk.structure;

import com.oldwei.hikdev.constant.HikConstant;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 13:44
 */
public class NET_DVR_ALARM_ISAPI_PICDATA extends Structure {
    public int dwPicLen;
    public byte byPicType;  //图片格式: 1- jpg
    public byte[] byRes = new byte[3];
    public byte[] szFilename = new byte[HikConstant.MAX_FILE_PATH_LEN];
    public Pointer pPicData; // 图片数据
}
