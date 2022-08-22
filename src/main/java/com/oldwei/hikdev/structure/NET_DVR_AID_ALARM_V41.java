package com.oldwei.hikdev.structure;

import com.oldwei.hikdev.constant.HikConstant;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 13:28
 */
public class NET_DVR_AID_ALARM_V41 extends Structure {
    public int dwSize;              //结构长度
    public int dwRelativeTime;        //相对时标
    public int dwAbsTime;            //绝对时标
    public NET_VCA_DEV_INFO struDevInfo = new NET_VCA_DEV_INFO();            //前端设备信息
    public NET_DVR_AID_INFO struAIDInfo = new NET_DVR_AID_INFO();         //交通事件信息
    public NET_DVR_SCENE_INFO struSceneInfo = new NET_DVR_SCENE_INFO();       //场景信息
    public int dwPicDataLen;        //图片长度
    public Pointer pImage;             //指向图片的指针
    // 0-数据直接上传; 1-云存储服务器URL(3.7Ver)原先的图片数据变成URL数据，图片长度变成URL长度
    public byte byDataType;
    public byte byLaneNo;  //关联车道号
    public short wMilliSecond;        //时标毫秒
    //监测点编号（路口编号、内部编号）
    public byte[] byMonitoringSiteID = new byte[HikConstant.MONITORSITE_ID_LEN/*48*/];
    public byte[] byDeviceID = new byte[HikConstant.DEVICE_ID_LEN/*48*/];//设备编号
    public int dwXmlLen;//XML报警信息长度
    public Pointer pXmlBuf;// XML报警信息指针,其XML对应到EventNotificationAlert XML Block
    public byte byTargetType;// 检测的目标类型，0~未知，1~行人、2~二轮车、3~三轮车(行人检测中返回)
    public byte[] byRes = new byte[19]; // 保留字节
}
