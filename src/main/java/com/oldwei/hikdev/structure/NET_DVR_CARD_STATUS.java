package com.oldwei.hikdev.structure;

import com.oldwei.hikdev.constant.HikConstant;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-13 17:49
 */
public class NET_DVR_CARD_STATUS extends Structure {
    public int   dwSize;
    public byte[] byCardNo = new byte[HikConstant.ACS_CARD_NO_LEN];
    public int   dwErrorCode;
    public byte    byStatus; // 状态：0-失败，1-成功
    public byte[] byRes = new byte[23];
}
