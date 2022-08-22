package com.oldwei.hikdev.structure;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 13:34
 */
public class NET_VCA_FACESNAP_INFO_ALARM extends Structure {
    public int dwRelativeTime;     // 相对时标
    public int dwAbsTime;            // 绝对时标
    public int dwSnapFacePicID;       //抓拍人脸图ID
    public int dwSnapFacePicLen;        //抓拍人脸子图的长度，为0表示没有图片，大于0表示有图片
    public NET_VCA_DEV_INFO struDevInfo = new NET_VCA_DEV_INFO();        //前端设备信息
    public byte byFaceScore;        //人脸评分，指人脸子图的质量的评分,0-100
    public byte bySex;//性别，0-未知，1-男，2-女
    public byte byGlasses;//是否带眼镜，0-未知，1-是，2-否
    //抓拍图片人脸年龄的使用方式，如byAge为15,byAgeDeviation为1,表示，实际人脸图片年龄的为14-16之间
    public byte byAge;//年龄
    public byte byAgeDeviation;//年龄误差值
    public byte byAgeGroup;//年龄段，详见HUMAN_AGE_GROUP_ENUM，若传入0xff表示未知
    public byte byFacePicQuality;
    public byte byRes1;              // 保留字节
    public int dwUIDLen; // 上传报警的标识长度
    public Pointer pUIDBuffer;  //标识指针
    public float fStayDuration;  //停留画面中时间(单位: 秒)
    public Pointer pBuffer1;  //抓拍人脸子图的图片数据
}
