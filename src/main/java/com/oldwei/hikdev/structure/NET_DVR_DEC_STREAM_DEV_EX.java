package com.oldwei.hikdev.structure;

import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2022/8/25 10:06
 */
public class NET_DVR_DEC_STREAM_DEV_EX extends Structure {
    public NET_DVR_STREAM_MEDIA_SERVER struStreamMediaSvrCfg = new NET_DVR_STREAM_MEDIA_SERVER();
    public NET_DVR_DEV_CHAN_INFO_EX struDevChanInfo = new NET_DVR_DEV_CHAN_INFO_EX();
}
