package com.oldwei.hikdev.sdk.structure;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 13:42
 */
public class NET_AIOP_VIDEO_HEAD extends Structure {
    public int dwSize;      //dwSize = sizeof(NET_AIOP_VIDEO_HEAD)
    public int dwChannel;    //设备分析通道的通道号；
    public NET_DVR_SYSTEM_TIME struTime = new NET_DVR_SYSTEM_TIME(); 	//时间
    public byte[] szTaskID = new byte[64];     //视频任务ID，来自于视频任务派发
    public int dwAIOPDataSize;   //对应AIOPDdata数据长度
    public int dwPictureSize;    //对应分析图片长度
    public byte[] szMPID = new byte[64];        //检测模型包ID，用于匹配AIOP的检测数据解析；可以通过URI(GET /ISAPI/Intelligent/AIOpenPlatform/algorithmModel/management?format=json)获取当前设备加载的模型包的label description信息；
    public Pointer pBufferAIOPData;  //AIOPDdata数据
    public Pointer pBufferPicture;//对应分析图片数据
    public byte byPictureMode;//图片数据传输模式 0-二进制，1-武汉云云存储，当byPictureMode为0时pBufferPicture为二进制数据，当byPictureMode为1时pBufferPicture为武汉云URL
    public byte[] byRes2 = new byte[3];//保留字节
    public int dwPresetIndex; //预置点序号
    public byte[] byRes = new byte[176];
}
