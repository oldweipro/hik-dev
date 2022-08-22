package com.oldwei.hikdev.structure;

import com.sun.jna.Structure;

/**
 * 单帧统计参数
 *
 * @author oldwei
 * @date 2021-5-18 13:21
 */
public class NET_DVR_STATFRAME extends Structure {
    public int dwRelativeTime;
    /**
     * 统计绝对时标
     */
    public int dwAbsTime;
    public byte[] byRes = new byte[92];
}
