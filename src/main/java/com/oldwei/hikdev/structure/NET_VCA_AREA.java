package com.oldwei.hikdev.structure;

import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 12:31
 */
public class NET_VCA_AREA extends Structure
{
    public NET_VCA_POLYGON struRegion;
    public byte[] byRes= new byte[8];
}
