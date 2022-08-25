package com.oldwei.hikdev.structure;

import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2022/8/24 18:34
 */
public class NET_DVR_RTSPCFG extends Structure {
    public int dwSize;         //长度
    public short wPort;          //rtsp服务器侦听端口
    public byte[] byReserve = new byte[54];  //预留
}
