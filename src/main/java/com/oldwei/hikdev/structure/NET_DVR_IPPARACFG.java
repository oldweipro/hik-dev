package com.oldwei.hikdev.structure;

import com.oldwei.hikdev.constant.HikConstant;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2022/8/25 9:17
 */
public class NET_DVR_IPPARACFG extends Structure {/* IP接入配置结构 */
    public  int dwSize;			                            /* 结构大小 */
    public  NET_DVR_IPDEVINFO[]  struIPDevInfo = new NET_DVR_IPDEVINFO[HikConstant.MAX_IP_DEVICE];    /* IP设备 */
    public   byte[] byAnalogChanEnable = new byte[HikConstant.MAX_ANALOG_CHANNUM];        /* 模拟通道是否启用，从低到高表示1-32通道，0表示无效 1有效 */
    public NET_DVR_IPCHANINFO[] struIPChanInfo = new NET_DVR_IPCHANINFO[HikConstant.MAX_IP_CHANNEL];	/* IP通道 */


}
