package com.oldwei.hikdev.structure;

import com.oldwei.hikdev.constant.HikConstant;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2022/8/24 19:11
 */
public class NET_DVR_IPDEVINFO_V31 extends Structure {
    public byte byEnable;/* 该通道是否启用 */
    public byte byProType;//协议类型(默认为私有协议)，0- 私有协议，1- 松下协议，2- 索尼，更多协议通过NET_DVR_GetIPCProtoList获取。
    public byte byEnableQuickAdd;//0-不支持快速添加；1-使用快速添加
    public byte byRes1;//保留，置为0
    public byte[] sUserName =new byte[HikConstant.NAME_LEN];//用户名
    public byte[] sPassword = new byte[HikConstant.PASSWD_LEN];//密码
    public byte[] byDomain = new byte[HikConstant.MAX_DOMAIN_NAME];//设备域名
    public NET_DVR_IPADDR struIP = new NET_DVR_IPADDR();//IP地址
    public short wDVRPort;//端口号
    public byte[] szDeviceID= new byte[32];
    public byte[] byRes2 = new byte[2];//保留，置为0
}
