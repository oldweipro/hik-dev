package com.oldwei.hikdev.sdk.structure;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 13:31
 */
public class NET_VCA_FACESNAP_RESULT extends Structure {
    public int dwSize;             // 结构大小
    public int dwRelativeTime;     // 相对时标
    public int dwAbsTime;            // 绝对时标
    public int dwFacePicID;       //人脸图ID
    public int dwFaceScore;        //人脸评分,0-100
    public NET_VCA_TARGET_INFO struTargetInfo = new NET_VCA_TARGET_INFO();//报警目标信息
    public NET_VCA_RECT struRect = new NET_VCA_RECT();      //人脸子图区域
    public NET_VCA_DEV_INFO struDevInfo = new NET_VCA_DEV_INFO();    //前端设备信息
    public int dwFacePicLen;        //人脸子图的长度，为0表示没有图片，大于0表示有图片
    public int dwBackgroundPicLen; //背景图的长度，为0表示没有图片，大于0表示有图片(保留)
    public byte bySmart;            //IDS设备返回0(默认值)，Smart Functiom Return 1
    public byte byAlarmEndMark;//报警结束标记0-保留，1-结束标记（该字段结合人脸ID字段使用，表示该ID对应的下报警结束，主要提供给NVR使用，用于判断报警结束，提取识别图片数据中，清晰度最高的图片）
    public byte byRepeatTimes;   //重复报警次数，0-无意义
    public byte byUploadEventDataType;//人脸图片数据长传方式：0-二进制数据，1-URL
    public NET_VCA_HUMAN_FEATURE struFeature = new NET_VCA_HUMAN_FEATURE();  //人体属性
    public float   fStayDuration;  //停留画面中时间(单位: 秒)
    public byte[] sStorageIP = new byte[16];        //存储服务IP地址
    public short wStoragePort;            //存储服务端口号
    public short wDevInfoIvmsChannelEx;     //与NET_VCA_DEV_INFO里的byIvmsChannel含义相同，能表示更大的值。老客户端用byIvmsChannel能继续兼容，但是最大到255。新客户端版本请使用wDevInfoIvmsChannelEx。
    public byte byFacePicQuality;
    public byte byUIDLen;     // 上传报警的标识长度
    public byte byLivenessDetectionStatus;// 活体检测状态：0-保留，1-未知(检测失败)，2-非真人人脸，3-真人人脸，4-未开启活体检测
    /*附加信息标识位（即是否有NET_VCA_FACESNAP_ADDINFO结构体）,0-无附加信息, 1-有附加信息。*/
    public byte byAddInfo;
    public Pointer pUIDBuffer;  //标识指针
    //附加信息指针,指向NET_VCA_FACESNAP_ADDINFO结构体
    public Pointer pAddInfoBuffer;
    public byte byTimeDiffFlag;      /*时差字段是否有效  0-时差无效， 1-时差有效 */
    public byte cTimeDifferenceH;         /*与UTC的时差（小时），-12 ... +14， +表示东区,，byTimeDiffFlag为1时有效*/
    public byte cTimeDifferenceM;      	/*与UTC的时差（分钟），-30, 30, 45， +表示东区，byTimeDiffFlag为1时有效*/
    public byte byBrokenNetHttp;     //断网续传标志位，0-不是重传数据，1-重传数据
    public Pointer pBuffer1;  //人脸子图的图片数据
    public Pointer pBuffer2;  //背景图的图片数据（保留，通过查找背景图接口可以获取背景图）
}
