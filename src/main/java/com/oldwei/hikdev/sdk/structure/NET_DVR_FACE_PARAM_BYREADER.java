package com.oldwei.hikdev.sdk.structure;

import com.oldwei.hikdev.constant.HikConstant;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-17 18:55
 */
public class NET_DVR_FACE_PARAM_BYREADER extends Structure {
    /**
     * 按值表示，人脸读卡器编号
     */
    public int dwCardReaderNo;
    /**
     * 是否删除所有卡的人脸信息，0-按卡号删除人脸信息，1-删除所有卡的人脸信息
     */
    public byte byClearAllCard;
    /**
     * 保留
     */
    public byte[] byRes1 = new byte[3];
    /**
     * 人脸关联的卡号
     */
    public byte[] byCardNo = new byte[HikConstant.ACS_CARD_NO_LEN];
    /**
     * 保留
     */
    public byte[] byRes = new byte[548];
}
