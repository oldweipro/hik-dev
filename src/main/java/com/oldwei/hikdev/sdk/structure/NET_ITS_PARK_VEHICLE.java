package com.oldwei.hikdev.sdk.structure;

import com.oldwei.hikdev.constant.HikConstant;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 13:23
 */
public class NET_ITS_PARK_VEHICLE extends Structure
{
    public int dwSize; //结构长度
    public byte byGroupNum; //图片组数量（单次轮询抓拍的图片数量）
    public byte byPicNo; //连拍的图片组上传图片序号（接收到图片组数量后，表示接收完成
    //接收超时不足图片组数量时，根据需要保留或删除）
    public byte byLocationNum; //单张图片所管理的车位数
    public byte byParkError; //停车异常，0-正常 1 异常
    public byte[] byParkingNo = new byte[HikConstant.MAX_PARKNO_LEN];//车位编号
    public byte byLocationStatus; //车位车辆状态，0-无车，1有车
    public byte bylogicalLaneNum;//逻辑车位号，0-3，一个相机最大能管4个车位 （0代表最左边，3代表最右边）
    public short wUpLoadType;//第零位表示：0~轮训上传、1~变化上传
    public byte[] byRes1 = new byte[4]; //保留字节
    public int dwChanIndex; //通道号数字通道
    public NET_DVR_PLATE_INFO struPlateInfo;  //车牌信息结构
    public NET_DVR_VEHICLE_INFO struVehicleInfo; //车辆信息
    public byte[] byMonitoringSiteID = new byte[HikConstant.MAX_ID_LEN]; //监测点编号
    public byte[] byDeviceID = new byte[HikConstant.MAX_ID_LEN]; //设备编号
    public int dwPicNum; //图片数量（与picGroupNum不同，代表本条信息附带的图片数量，图片信息由struVehicleInfoEx定义
    public NET_ITS_PICTURE_INFO[] struPicInfo = new NET_ITS_PICTURE_INFO[2];  //图片信息,单张回调，最多2张图，由序号区分
    public byte[] byRes2 = new byte[256];
}
