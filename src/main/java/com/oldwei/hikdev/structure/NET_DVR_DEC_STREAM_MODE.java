package com.oldwei.hikdev.structure;

import com.sun.jna.Union;

/**
 * @author oldwei
 * @date 2022/8/25 10:05
 */
public class NET_DVR_DEC_STREAM_MODE extends Union {
    public NET_DVR_DEC_STREAM_DEV_EX struDecStreamDev = new NET_DVR_DEC_STREAM_DEV_EX();
    public NET_DVR_PU_STREAM_URL struUrlInfo = new NET_DVR_PU_STREAM_URL();
    public NET_DVR_DEC_DDNS_DEV struDdnsDecInfo = new NET_DVR_DEC_DDNS_DEV();
    public byte[] byRes = new byte[300];
}