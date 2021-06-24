package com.oldwei.hikdev.sdk.structure;

import com.oldwei.hikdev.constant.HikConstant;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 13:27
 */
public class NET_DVR_AID_INFO extends Structure {
    public byte byRuleID;   // 规则序号，为规则配置结构下标，0-16
    public byte[] byRes1 = new byte[3];
    public byte[] byRuleName = new byte[HikConstant.NAME_LEN]; //  规则名称
    public int dwAIDType;  // 报警事件类型
    public NET_DVR_DIRECTION struDirect = new NET_DVR_DIRECTION(); // 报警指向区域
    public byte bySpeedLimit; //限速值，单位km/h[0,255]
    public byte byCurrentSpeed; //当前速度值，单位km/h[0,255]
    public byte byVehicleEnterState; //车辆出入状态：0- 无效，1- 驶入，2- 驶出
    public byte byState; //0-变化上传，1-轮巡上传
    public byte[] byParkingID = new byte[16]; //停车位编号
    public byte[] byRes2 = new byte[20];  // 保留字节
}
