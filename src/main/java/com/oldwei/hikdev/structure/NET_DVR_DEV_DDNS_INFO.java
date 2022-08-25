package com.oldwei.hikdev.structure;

import com.oldwei.hikdev.constant.HikConstant;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2022/8/25 10:08
 */
public class NET_DVR_DEV_DDNS_INFO extends Structure {
    public byte[] byDevAddress = new byte[HikConstant.MAX_DOMAIN_NAME];    //域名(IPServer或hiDDNS时可填序列号或者别名)
    public byte byTransProtocol;        //传输协议类型0-TCP，1-UDP, 2-MCAST
    public byte byTransMode;            //传输码流模式 0－主码流 1－子码流
    public byte byDdnsType;         //域名服务器类型，0-IPServer 1－Dyndns 2－PeanutHull(花生壳)，3- NO-IP, 4- hiDDNS
    public byte byRes1;
    public byte[] byDdnsAddress = new byte[HikConstant.MAX_DOMAIN_NAME];  //DDNS服务器地址
    public short wDdnsPort;                 //DDNS服务器端口号
    public byte byChanType;              //0-普通通道,1-零通道,2-流ID
    public byte byFactoryType;            //前端设备厂家类型,通过接口获取
    public int dwChannel; //通道号
    public byte[] byStreamId = new byte[HikConstant.STREAM_ID_LEN]; //流ID
    public byte[] sUserName = new byte[HikConstant.NAME_LEN];    //监控主机登陆帐号
    public byte[] sPassword = new byte[HikConstant.PASSWD_LEN];    //监控主机密码
    public short wDevPort;                //前端设备通信端口
    public byte[] byRes2 = new byte[2];
}
