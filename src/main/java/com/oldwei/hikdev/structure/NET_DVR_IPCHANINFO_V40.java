package com.oldwei.hikdev.structure;

import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2022/8/24 19:15
 */
public class NET_DVR_IPCHANINFO_V40 extends Structure {

    public byte byEnable;//IP通道在线状态，是一个只读的属性；
    //0表示HDVR或者NVR设备的数字通道连接对应的IP设备失败，该通道不在线；1表示连接成功，该通道在线
    public byte byRes1;//保留，置为0
    public short wIPID;//IP设备ID
    public int dwChannel;//IP设备的通道号，例如设备A（HDVR或者NVR设备）的IP通道01，对应的是设备B（DVS）里的通道04，则byChannel=4，如果前端接的是IPC则byChannel=1。
    public byte byTransProtocol;//传输协议类型：0- TCP，1- UDP，2- 多播，0xff- auto(自动)
    public byte byTransMode;//传输码流模式：0- 主码流，1- 子码流
    public byte byFactoryType;//前端设备厂家类型
    public byte[] byRes = new byte[241];//保留，置为0
}