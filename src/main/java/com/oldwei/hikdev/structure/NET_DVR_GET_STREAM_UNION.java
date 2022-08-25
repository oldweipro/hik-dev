package com.oldwei.hikdev.structure;

import com.sun.jna.Union;

/**
 * @author oldwei
 * @date 2022/8/24 19:14
 */
public class NET_DVR_GET_STREAM_UNION extends Union {
    public NET_DVR_IPCHANINFO struChanInfo = new NET_DVR_IPCHANINFO(); /*IP通道信息*/
    public NET_DVR_IPCHANINFO_V40 struIPChan = new NET_DVR_IPCHANINFO_V40(); //直接从设备取流（扩展）
    public byte[] byUnionLen = new byte[492]; //直接从设备取流（扩展）
}