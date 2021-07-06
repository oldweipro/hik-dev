package com.oldwei.hikdev.structure;

import com.sun.jna.Structure;

/**
 * 单帧统计参数
 * @author oldwei
 * @date 2021-5-18 13:21
 */
public class NET_DVR_STATTIME extends Structure {
    /**
     * 统计开始时间
     */
    public NET_DVR_TIME tmStart;
    /**
     * 统计结束时间
     */
    public NET_DVR_TIME tmEnd;
    public byte[] byRes = new byte[92];
}
