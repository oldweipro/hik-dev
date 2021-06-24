package com.oldwei.hikdev.sdk.structure;

import com.oldwei.hikdev.constant.HikConstant;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-17 18:07
 */
public class NET_DVR_FACE_PARAM_BYCARD extends Structure {
    /**
     * 人脸关联的卡号
     */
    public byte[] byCardNo = new byte[HikConstant.ACS_CARD_NO_LEN];
    /**
     * 人脸的读卡器信息，按数组表示
     */
    public byte[] byEnableCardReader = new byte[HikConstant.MAX_CARD_READER_NUM_512];
    /**
     * 需要删除的人脸编号，按数组下标，值表示0-不删除，1-删除该人脸
     */
    public byte[] byFaceID = new byte[HikConstant.MAX_FACE_NUM];
    /**
     * 保留
     */
    public byte[] byRes1 = new byte[42];
}
