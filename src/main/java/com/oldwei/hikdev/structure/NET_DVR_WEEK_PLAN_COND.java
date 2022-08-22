package com.oldwei.hikdev.structure;

import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-8-16 16:46
 */
public class NET_DVR_WEEK_PLAN_COND extends Structure {
    public int dwSize;
    public int dwWeekPlanNumber; //周计划编号
    public short wLocalControllerID; //就地控制器序号[1,64]
    public byte[] byRes = new byte[106];
}
