package com.oldwei.hikdev.sdk.structure;

import com.oldwei.hikdev.sdk.constant.HikConstant;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 13:29
 */
public class NET_DVR_TPS_ALARM_V41 extends Structure {
    public int dwSize;          // 结构体大小
    public int dwRelativeTime;  // 相对时标
    public int dwAbsTime;       // 绝对时标
    public NET_VCA_DEV_INFO struDevInfo;     // 前端设备信息
    public NET_DVR_TPS_INFO_V41 struTPSInfo;     // 交通参数统计信息
    //监测点编号（路口编号、内部编号）
    public byte[] byMonitoringSiteID = new byte[HikConstant.MONITORSITE_ID_LEN/*48*/];
    public byte[] byDeviceID = new byte[HikConstant.DEVICE_ID_LEN/*48*/];//设备编号
    public int dwStartTime;  // 开始统计时间
    public int dwStopTime;    // 结束统计时间
    public byte[] byRes = new byte[24];      // 保留
}
