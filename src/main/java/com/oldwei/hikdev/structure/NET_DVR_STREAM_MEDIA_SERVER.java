package com.oldwei.hikdev.structure;

import com.oldwei.hikdev.constant.HikConstant;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2022/8/25 10:06
 */
public class NET_DVR_STREAM_MEDIA_SERVER extends Structure {
    public byte byValid; //是否启用，0-否，1-是
    public byte[] byRes1 = new byte[3];
    public byte[] byAddress = new byte[HikConstant.MAX_DOMAIN_NAME];   //IP或者域名
    public short wDevPort;            /*流媒体服务器端口*/
    public byte byTransmitType;        /*传输协议类型 0-TCP，1-UDP*/
    public byte[] byRes2 = new byte[5];
}