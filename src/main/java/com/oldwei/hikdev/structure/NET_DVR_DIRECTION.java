package com.oldwei.hikdev.structure;

import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 13:27
 */
public class NET_DVR_DIRECTION extends Structure {
    public NET_VCA_POINT struStartPoint = new NET_VCA_POINT();   // 方向起始点
    public NET_VCA_POINT struEndPoint = new NET_VCA_POINT();     // 方向结束点
}
