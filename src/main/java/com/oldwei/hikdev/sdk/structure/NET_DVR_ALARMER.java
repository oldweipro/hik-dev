package com.oldwei.hikdev.sdk.structure;

import com.oldwei.hikdev.sdk.constant.HikConstant;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 12:02
 */
public class NET_DVR_ALARMER extends Structure {
    /**
     * userid是否有效 0-无效，1-有效
     */
    public byte byUserIDValid;
    /**
     * 序列号是否有效 0-无效，1-有效
     */
    public byte bySerialValid;
    /**
     * 版本号是否有效 0-无效，1-有效
     */
    public byte byVersionValid;
    /**
     * 设备名字是否有效 0-无效，1-有效
     */
    public byte byDeviceNameValid;
    /**
     * MAC地址是否有效 0-无效，1-有效
     */
    public byte byMacAddrValid;
    /**
     * login端口是否有效 0-无效，1-有效
     */
    public byte byLinkPortValid;
    /**
     * 设备IP是否有效 0-无效，1-有效
     */
    public byte byDeviceIPValid;
    /**
     * socket ip是否有效 0-无效，1-有效
     */
    public byte bySocketIPValid;
    /**
     * NET_DVR_Login()返回值, 布防时有效
     */
    public int lUserID;
    /**
     * 序列号
     */
    public byte[] sSerialNumber = new byte[HikConstant.SERIALNO_LEN];
    /**
     * 版本信息 高16位表示主版本，低16位表示次版本
     */
    public int dwDeviceVersion;
    /**
     * 设备名字
     */
    public byte[] sDeviceName = new byte[HikConstant.NAME_LEN];
    /**
     * MAC地址
     */
    public byte[] byMacAddr = new byte[HikConstant.MACADDR_LEN];
    /**
     * link port
     */
    public short wLinkPort;
    /**
     * IP地址
     */
    public byte[] sDeviceIP = new byte[128];
    /**
     * 报警主动上传时的socket IP地址
     */
    public byte[] sSocketIP = new byte[128];
    /**
     * Ip协议 0-IPV4, 1-IPV6
     */
    public byte byIpProtocol;
    public byte[] byRes2 = new byte[11];
}
