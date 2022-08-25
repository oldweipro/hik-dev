package com.oldwei.hikdev.structure;

import com.oldwei.hikdev.constant.HikConstant;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2022/8/24 19:03
 */
public class NET_DVR_IPPARACFG_V40 extends Structure {/* IP接入配置结构V40 */
    public int dwSize;   /* 结构大小 */
    public int dwGroupNum;//设备支持的总组数（只读）。
    public int dwAChanNum;//最大模拟通道个数（只读）
    public int dwDChanNum;//数字通道个数（只读）
    public int dwStartDChan;//起始数字通道（只读）
    public byte[] byAnalogChanEnable = new byte[HikConstant.MAX_CHANNUM_V30]; //模拟通道资源是否启用，从低到高表示1-64通道：0-禁用，1-启用。
    public NET_DVR_IPDEVINFO_V31[] struIPDevInfo = new NET_DVR_IPDEVINFO_V31[HikConstant.MAX_IP_DEVICE_V40];//IP设备信息，下标0对应设备IP ID为1
    public NET_DVR_STREAM_MODE[] struStreamMode = new NET_DVR_STREAM_MODE[HikConstant.MAX_CHANNUM_V30];//取流模式
    public byte[] byRes2 = new byte[20];//保留，置为0
}
