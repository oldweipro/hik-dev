package com.oldwei.hikdev.structure;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 13:34
 */
public class NET_VCA_BLOCKLIST_INFO_ALARM extends Structure {
    public NET_VCA_BLOCKLIST_INFO struBlockListInfo = new NET_VCA_BLOCKLIST_INFO(); //禁止名单基本信息
    public int dwBlockListPicLen;       //禁止名单人脸子图的长度，为0表示没有图片，大于0表示有图片
    public int dwFDIDLen;// 人脸库ID长度
    public Pointer pFDID;  //人脸库Id指针
    public int dwPIDLen;// 人脸库图片ID长度
    public Pointer pPID;  //人脸库图片ID指针
    public short wThresholdValue; //人脸库阈值[0,100]
    public byte byIsNoSaveFDPicture;//0-保存人脸库图片,1-不保存人脸库图片, 若开启了导入图片或者建模时不保存原图功能时,该字段返回1,此时人脸库图片将不再返回
    public byte byRealTimeContrast;//是否实时报警 0-实时 1-非实时
    public Pointer pBuffer1;  //禁止名单人脸子图的图片数据
}
