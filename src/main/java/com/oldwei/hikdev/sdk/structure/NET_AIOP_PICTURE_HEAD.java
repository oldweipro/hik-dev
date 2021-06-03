package com.oldwei.hikdev.sdk.structure;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 13:43
 */
public class NET_AIOP_PICTURE_HEAD extends Structure {
    public int dwSize;           //dwSize = sizeof(NET_AIOP_PICTURE_HEAD)
    public NET_DVR_SYSTEM_TIME struTime = new NET_DVR_SYSTEM_TIME(); 	//时间
    public byte[] szPID = new byte[64];        //透传下发的图片ID，来自于图片任务派发
    public int dwAIOPDataSize;   //对应AIOPDdata数据长度
    public byte byStatus;         //状态值：0-成功，1-图片大小错误
    public byte[] byRes1 = new byte[3];
    public byte[] szMPID = new byte[64]; //检测模型包ID，用于匹配AIOP的检测数据解析；
    public Pointer pBufferAIOPData;//AIOPDdata数据
    public int dwPresetIndex; //预置点序号
    public byte[] byRes = new byte[180];
}
