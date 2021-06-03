package com.oldwei.hikdev.sdk.structure;

import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 13:30
 */
public class NET_DVR_LANE_QUEUE extends Structure {
    public NET_VCA_POINT struHead;       //队列头
    public NET_VCA_POINT struTail;       //队列尾
    public int dwLength;      //实际队列长度 单位为米 [0-500]
}
