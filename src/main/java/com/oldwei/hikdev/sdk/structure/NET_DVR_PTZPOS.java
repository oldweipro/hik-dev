package com.oldwei.hikdev.sdk.structure;

import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 13:27
 */
public class NET_DVR_PTZPOS extends Structure
{
    public  short wAction;//获取时该字段无效
    public  short wPanPos;//水平参数
    public  short wTiltPos;//垂直参数
    public  short wZoomPos;//变倍参数
}
