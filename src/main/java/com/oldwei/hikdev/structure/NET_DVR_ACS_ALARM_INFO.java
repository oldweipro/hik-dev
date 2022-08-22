package com.oldwei.hikdev.structure;

import com.oldwei.hikdev.constant.HikConstant;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 13:38
 */
public class NET_DVR_ACS_ALARM_INFO extends Structure {
    public int dwSize;
    public int dwMajor; //报警主类型，参考宏定义
    public int dwMinor; //报警次类型，参考宏定义{1024:防区短路报警,21:门锁打开,22:门锁关闭}
    public NET_DVR_TIME struTime = new NET_DVR_TIME(); //时间
    public byte[] sNetUser = new byte[HikConstant.MAX_NAMELEN] ;//网络操作的用户名
    public NET_DVR_IPADDR struRemoteHostAddr = new NET_DVR_IPADDR();//远程主机地址
    public NET_DVR_ACS_EVENT_INFO struAcsEventInfo = new NET_DVR_ACS_EVENT_INFO(); //详细参数
    public int dwPicDataLen;   //图片数据大小，不为0是表示后面带数据
    public Pointer pPicData;
    public short wInductiveEventType; //归纳事件类型，0-无效，客户端判断该值为非0值后，报警类型通过归纳事件类型区分，否则通过原有报警主次类型（dwMajor、dwMinor）区分
    public byte byPicTransType;        //图片数据传输方式: 0-二进制；1-url
    public byte byRes1;             //保留字节
    public int dwIOTChannelNo;    //IOT通道号
    public Pointer pAcsEventInfoExtend;    //byAcsEventInfoExtend为1时，表示指向一个NET_DVR_ACS_EVENT_INFO_EXTEND结构体
    public byte byAcsEventInfoExtend;    //pAcsEventInfoExtend是否有效：0-无效，1-有效
    public byte byTimeType; //时间类型：0-设备本地时间，1-UTC时间（struTime的时间）
    public byte byRes2;             //保留字节
    public byte byAcsEventInfoExtendV20;    //pAcsEventInfoExtendV20是否有效：0-无效，1-有效
    public Pointer pAcsEventInfoExtendV20;    //byAcsEventInfoExtendV20为1时，表示指向一个NET_DVR_ACS_EVENT_INFO_EXTEND_V20结构体
    public byte[] byRes = new byte[4];
}
