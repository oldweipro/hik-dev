package com.oldwei.hikdev.sdk.structure;

import com.oldwei.hikdev.constant.HikConstant;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-13 10:28
 */
public class NET_DVR_CARD_RECORD extends Structure {
    public int  dwSize;
    public byte[] byCardNo = new byte[HikConstant.ACS_CARD_NO_LEN];
    public byte   byCardType;
    public byte   byLeaderCard;
    public byte   byUserType;
    public byte   byRes1;
    public byte[] byDoorRight = new byte[HikConstant.MAX_DOOR_NUM_256];
    public NET_DVR_VALID_PERIOD_CFG struValid = new NET_DVR_VALID_PERIOD_CFG();
    public byte[] byBelongGroup = new byte[HikConstant.MAX_GROUP_NUM_128];
    public byte[] byCardPassword= new byte[HikConstant.CARD_PASSWORD_LEN];
    public short[] wCardRightPlan = new short[HikConstant.MAX_DOOR_NUM_256];
    public int  dwMaxSwipeTimes;
    public int  dwSwipeTimes;
    public int  dwEmployeeNo;
    public byte[] byName = new byte[HikConstant.NAME_LEN];
    //按位表示，0-无权限，1-有权限
    //第0位表示：弱电报警
    //第1位表示：开门提示音
    //第2位表示：限制客卡
    //第3位表示：通道
    //第4位表示：反锁开门
    //第5位表示：巡更功能
    public int dwCardRight;
    public byte[] byRes = new byte[256];
}
