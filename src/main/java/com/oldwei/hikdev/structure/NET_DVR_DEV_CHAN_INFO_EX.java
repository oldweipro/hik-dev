package com.oldwei.hikdev.structure;

import com.oldwei.hikdev.constant.HikConstant;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2022/8/25 10:06
 */
public class NET_DVR_DEV_CHAN_INFO_EX extends Structure {
    public byte byChanType;              //通道类型，0-普通通道,1-零通道,2-流ID，3-本地输入源，4-虚拟屏服务器通道，5-拼接通道，6-屏幕服务器，7-分布式网络源，8-多相机融合通道，9-网络输入源
    public byte[] byStreamId = new byte[HikConstant.STREAM_ID_LEN]; //流ID，当byChanType=2、9时，该字段用于指定流或者网络ipc的ID号
    public byte[] byRes1 = new byte[3];
    public int dwChannel;  //通道号，通道类型为普通通道，零通道，本地输入源，虚拟屏服务器通道，拼接通道，屏幕服务器，分布式网络源时填此字段
    public byte[] byRes2 = new byte[24];
    public byte[] byAddress = new byte[HikConstant.MAX_DOMAIN_NAME];    //设备域名
    public short wDVRPort;                 //端口号
    public byte byChannel;                //通道号,dwChannel不为0时此字段无效
    public byte byTransProtocol;        //传输协议类型0-TCP，1-UDP
    public byte byTransMode;            //传输码流模式 0－主码流 1－子码流
    public byte byFactoryType;            /*前端设备厂家类型,通过接口获取*/
    public byte byDeviceType; //设备类型(视频综合平台智能板使用)，1-解码器（此时根据视频综合平台能力集中byVcaSupportChanMode字段来决定是使用解码通道还是显示通道），2-编码器
    public byte byDispChan;//显示通道号,智能配置使用
    public byte bySubDispChan;//显示通道子通道号，智能配置时使用
    public byte byResolution;    //; 1-CIF 2-4CIF 3-720P 4-1080P 5-500w大屏控制器使用，大屏控制器会根据该参数分配解码资源
    public byte[] byRes = new byte[2];
    public byte[] sUserName = new byte[HikConstant.NAME_LEN];    //监控主机登陆帐号
    public byte[] sPassword = new byte[HikConstant.PASSWD_LEN];    //监控主机密码
}