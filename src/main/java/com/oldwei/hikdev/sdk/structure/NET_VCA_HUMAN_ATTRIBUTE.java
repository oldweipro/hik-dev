package com.oldwei.hikdev.sdk.structure;

import com.oldwei.hikdev.constant.HikConstant;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 13:35
 */
public class NET_VCA_HUMAN_ATTRIBUTE extends Structure {
    public byte bySex; //性别：0-男，1-女
    public byte byCertificateType; //证件类型：0-身份证，1-警官证
    public byte[] byBirthDate = new byte[HikConstant.MAX_HUMAN_BIRTHDATE_LEN]; //出生年月，如：201106
    public byte[] byName = new byte[HikConstant.NAME_LEN]; //姓名
    public NET_DVR_AREAINFOCFG struNativePlace = new NET_DVR_AREAINFOCFG(); //籍贯参数
    public byte[] byCertificateNumber = new byte[HikConstant.NAME_LEN];  //证件号
    public int dwPersonInfoExtendLen;// 人员标签信息扩展长度
    public Pointer pPersonInfoExtend;  //人员标签信息扩展信息
    public byte byAgeGroup;//年龄段，详见HUMAN_AGE_GROUP_ENUM，如传入0xff表示未知
    public byte[] byRes2 = new byte[11];
}
