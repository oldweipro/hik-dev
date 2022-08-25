package com.oldwei.hikdev.structure;

import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2022/8/24 19:15
 */
public class NET_DVR_IPCHANINFO extends Structure {/* IP通道匹配参数 */
    public   byte byEnable;					/* 该通道是否启用 */
    public  byte byIPID;					/* IP设备ID 取值1- MAX_IP_DEVICE */
    public  byte byChannel;					/* 通道号 */
    public   byte[] byres = new byte[33];					/* 保留 */
}