package com.oldwei.hikdev.sdk.structure;

import com.oldwei.hikdev.sdk.constant.HikConstant;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 13:35
 */
public class NET_VCA_BLOCKLIST_INFO extends Structure {
    public int dwSize;   //结构大小
    public int dwRegisterID;  //名单注册ID号（只读）
    public int dwGroupNo; //分组号
    public byte byType; //允许名单标志：0-全部，1-允许名单，2-禁止名单
    public byte byLevel; //禁止名单等级，0-全部，1-低，2-中，3-高
    public byte[] byRes1 = new byte[2];  //保留
    public NET_VCA_HUMAN_ATTRIBUTE struAttribute = new NET_VCA_HUMAN_ATTRIBUTE();  //人员信息
    public byte[] byRemark = new byte[HikConstant.NAME_LEN]; //备注信息
    public int dwFDDescriptionLen;//人脸库描述数据长度
    public Pointer pFDDescriptionBuffer;//人脸库描述数据指针
    public int dwFCAdditionInfoLen;//抓拍库附加信息长度
    public Pointer pFCAdditionInfoBuffer;//抓拍库附加信息数据指针（FCAdditionInfo中包含相机PTZ坐标）
    public byte[] byRes2 = new byte[4];
}
