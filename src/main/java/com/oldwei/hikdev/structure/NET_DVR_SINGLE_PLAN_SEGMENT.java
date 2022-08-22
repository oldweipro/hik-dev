package com.oldwei.hikdev.structure;

import com.oldwei.hikdev.service.HCNetSDK;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-8-16 16:45
 */
public class NET_DVR_SINGLE_PLAN_SEGMENT extends Structure {
    public byte byEnable; //是否使能，1-使能，0-不使能
    public byte byDoorStatus; //门状态模式（梯控模式），0-无效，1-常开状态（自由），2-常闭状态（禁用），3-普通状态（门状态计划使用）
    public byte byVerifyMode; //验证方式，0-无效，1-刷卡，2-刷卡+密码(读卡器验证方式计划使用)，3-刷卡,4-刷卡或密码(读卡器验证方式计划使用), 5-指纹，6-指纹+密码，7-指纹或刷卡，8-指纹+刷卡，9-指纹+刷卡+密码（无先后顺序），10-人脸或指纹或刷卡或密码，11-人脸+指纹，12-人脸+密码，
    //13-人脸+刷卡，14-人脸，15-工号+密码，16-指纹或密码，17-工号+指纹，18-工号+指纹+密码，19-人脸+指纹+刷卡，20-人脸+密码+指纹，21-工号+人脸，22-人脸或人脸+刷卡
    public byte[] byRes = new byte[5];
    public HCNetSDK.NET_DVR_TIME_SEGMENT struTimeSegment; //时间段参数
}
