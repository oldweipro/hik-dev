package com.oldwei.hikdev.sdk.structure;

import com.oldwei.hikdev.sdk.constant.HikConstant;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 12:32
 */
public class NET_VCA_POLYGON extends Structure
{
    public int dwPointNum;
    public NET_VCA_POINT[] struPos= new NET_VCA_POINT[HikConstant.VCA_MAX_POLYGON_POINT_NUM];
}
