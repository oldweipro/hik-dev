package com.oldwei.hikdev.structure;

import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2022/8/25 9:51
 */
public class NET_DVR_PU_STREAM_URL extends Structure {
    public byte byEnable;//是否启用：0- 禁用，1- 启用
    public byte[] strURL = new byte[240];//取流URL路径
    public byte byTransPortocol;//传输协议类型：0-TCP，1-UDP
    public short wIPID;//设备ID号，wIPID = iDevInfoIndex + iGroupNO*64 +1
    public byte byChannel;//设备通道号
    public byte[] byRes = new byte[7];//保留，置为0
}
