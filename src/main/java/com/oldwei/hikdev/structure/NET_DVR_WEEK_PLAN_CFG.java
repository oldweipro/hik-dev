package com.oldwei.hikdev.structure;

import com.oldwei.hikdev.constant.HikConstant;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-8-16 16:46
 */
public class NET_DVR_WEEK_PLAN_CFG extends Structure {
    public int dwSize;
    public byte byEnable;  //是否使能，1-使能，0-不使能
    public byte[] byRes1 = new byte[3];
    public NET_DVR_SINGLE_PLAN_SEGMENT_WEEK[] struPlanCfg = new NET_DVR_SINGLE_PLAN_SEGMENT_WEEK[HikConstant.MAX_DAYS]; //周计划参数
    public byte[] byRes2 = new byte[16];
}
