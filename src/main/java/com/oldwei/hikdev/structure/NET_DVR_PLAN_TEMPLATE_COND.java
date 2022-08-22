package com.oldwei.hikdev.structure;

import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-8-16 16:45
 */
public class NET_DVR_PLAN_TEMPLATE_COND extends Structure {
    public int dwSize;
    public int dwPlanTemplateNumber; //计划模板编号，从1开始，最大值从门禁能力集获取
    public short wLocalControllerID; //就地控制器序号[1,64]，0无效
    public byte[] byRes = new byte[106];
}
