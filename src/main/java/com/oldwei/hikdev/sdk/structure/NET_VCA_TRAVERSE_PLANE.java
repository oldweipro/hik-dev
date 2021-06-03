package com.oldwei.hikdev.sdk.structure;

import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 12:30
 */
public class NET_VCA_TRAVERSE_PLANE extends Structure {
    public NET_VCA_LINE struPlaneBottom;
    public int dwCrossDirection;
    public byte bySensitivity;
    public byte byPlaneHeight;
    /**
     * 检测目标：0- 所有目标，1- 人，2- 车
     */
    public byte byDetectionTarget;
    public byte[] byRes2 = new byte[37];
}
