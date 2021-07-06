package com.oldwei.hikdev.structure;

import com.sun.jna.Structure;

/**
 * 有效期参数结构体
 *
 * @author oldwei
 * @date 2021-5-13 10:31
 */
public class NET_DVR_VALID_PERIOD_CFG extends Structure {
    public byte byEnable;
    public byte[] byRes1 = new byte[3];
    public NET_DVR_TIME_EX struBeginTime;
    public NET_DVR_TIME_EX struEndTime;
    public byte[] byRes2 = new byte[32];
}
