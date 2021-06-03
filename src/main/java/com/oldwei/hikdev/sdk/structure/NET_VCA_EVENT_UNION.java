package com.oldwei.hikdev.sdk.structure;

import com.sun.jna.Union;

/**
 * @author oldwei
 * @date 2021-5-18 12:30
 */
public class NET_VCA_EVENT_UNION extends Union
{
    public int[] uLen = new int[23];
    public NET_VCA_TRAVERSE_PLANE struTraversePlane;
    public NET_VCA_AREA struArea;
}
