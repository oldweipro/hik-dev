package com.oldwei.hikdev.sdk.structure;

import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 13:32
 */
public class NET_VCA_HUMAN_FEATURE extends Structure {
    public byte byAgeGroup;    //年龄段,参见 HUMAN_AGE_GROUP_ENUM
    public byte bySex;         //性别, 0-表示“未知”（算法不支持）,1 – 男 , 2 – 女, 0xff-算法支持，但是没有识别出来
    public byte byEyeGlass;    //是否戴眼镜 0-表示“未知”（算法不支持）,1 – 不戴, 2 – 戴,0xff-算法支持，但是没有识别出来
    //抓拍图片人脸年龄的使用方式，如byAge为15,byAgeDeviation为1,表示，实际人脸图片年龄的为14-16之间
    public byte byAge;//年龄 0-表示“未知”（算法不支持）,0xff-算法支持，但是没有识别出来
    public byte byAgeDeviation;//年龄误差值
    public byte byRes0;   //字段预留
    public byte byMask;       //是否戴口罩 0-表示“未知”（算法不支持）,1 – 不戴, 2 – 戴, 0xff-算法支持，但是没有识别出来
    public byte bySmile;      //是否微笑 0-表示“未知”（算法不支持）,1 – 不微笑, 2 – 微笑, 0xff-算法支持，但是没有识别出来
    public byte byFaceExpression;    /* 表情,参见FACE_EXPRESSION_GROUP_ENUM*/
    public byte byRes1;
    public byte byRes2;
    public byte byHat; // 帽子, 0-不支持,1-不戴帽子,2-戴帽子,0xff-unknow表示未知,算法支持未检出
    public byte[] byRes = new byte[4];    //保留
}
