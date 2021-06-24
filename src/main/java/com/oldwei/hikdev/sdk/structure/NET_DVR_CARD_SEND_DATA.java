package com.oldwei.hikdev.sdk.structure;

import com.oldwei.hikdev.constant.HikConstant;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-17 18:17
 */
public class NET_DVR_CARD_SEND_DATA extends Structure {
    public int dwSize;
    /**
     * 卡号
     */
    public byte[] byCardNo = new byte[HikConstant.ACS_CARD_NO_LEN];
    public byte[] byRes = new byte[16];
}
