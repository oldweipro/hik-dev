package com.oldwei.hikdev.structure;

import com.oldwei.hikdev.constant.HikConstant;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-8-16 16:45
 */
public class NET_DVR_PLAN_TEMPLATE extends Structure {
    public int dwSize;
    public byte byEnable; //是否启用，1-启用，0-不启用
    public byte[] byRes1 = new byte[3];
    public byte[] byTemplateName = new byte[HikConstant.TEMPLATE_NAME_LEN]; //模板名称
    public int dwWeekPlanNo; //周计划编号，0为无效
    public int[] dwHolidayGroupNo = new int[HikConstant.MAX_HOLIDAY_GROUP_NUM]; //假日组编号，就前填充，遇0无效
    public byte[] byRes2 = new byte[32];
}
