package com.oldwei.hikdev.structure;

import com.oldwei.hikdev.constant.HikConstant;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 13:40
 */
public class NET_DVR_ID_CARD_INFO_ALARM extends Structure {
    public int dwSize;        //结构长度
    public NET_DVR_ID_CARD_INFO struIDCardCfg = new NET_DVR_ID_CARD_INFO();//身份证信息
    public int dwMajor; //报警主类型，参考宏定义
    public int dwMinor; //报警次类型，参考宏定义
    public NET_DVR_TIME_V30 struSwipeTime = new NET_DVR_TIME_V30(); //时间
    public byte[] byNetUser = new byte[HikConstant.MAX_NAMELEN] ;//网络操作的用户名
    public NET_DVR_IPADDR struRemoteHostAddr = new NET_DVR_IPADDR();//远程主机地址
    public int dwCardReaderNo; //读卡器编号，为0无效
    public int dwDoorNo; //门编号，为0无效
    public int dwPicDataLen;   //图片数据大小，不为0是表示后面带数据
    public Pointer pPicData;
    public byte byCardType; //卡类型，1-普通卡，2-残障人士卡，3-禁止名单卡，4-巡更卡，5-胁迫卡，6-超级卡，7-来宾卡，8-解除卡，为0无效
    public byte byDeviceNo;                             // 设备编号，为0时无效（有效范围1-255）
    public byte byMask; //是否带口罩：0-保留，1-未知，2-不戴口罩，3-戴口罩
    public byte byCurrentEvent; //是否为实时事件：0-无效，1-是（实时事件），2-否（离线事件）
    public int dwFingerPrintDataLen;                  // 指纹数据大小，不为0是表示后面带数据
    public Pointer pFingerPrintData;
    public int dwCapturePicDataLen;                   // 抓拍图片数据大小，不为0是表示后面带数据
    public Pointer pCapturePicData;
    public int dwCertificatePicDataLen;   //证件抓拍图片数据大小，不为0是表示后面带数据
    public Pointer pCertificatePicData;
    public byte byCardReaderKind; //读卡器属于哪一类，0-无效，1-IC读卡器，2-身份证读卡器，3-二维码读卡器,4-指纹头
    public byte[] byRes3 = new byte[2];
    public byte byIDCardInfoExtend;    //pIDCardInfoExtend是否有效：0-无效，1-有效
    public Pointer pIDCardInfoExtend;    //byIDCardInfoExtend为1时，表示指向一个NET_DVR_ID_CARD_INFO_EXTEND结构体
    public int dwSerialNo; //事件流水号，为0无效
    public byte[] byRes = new byte[168];
}
