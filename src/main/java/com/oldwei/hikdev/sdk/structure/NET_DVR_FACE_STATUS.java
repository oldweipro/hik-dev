package com.oldwei.hikdev.sdk.structure;

import com.oldwei.hikdev.sdk.constant.HikConstant;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-17 16:55
 */
public class NET_DVR_FACE_STATUS extends Structure {
    public int dwSize;
    public byte[] byCardNo = new byte[HikConstant.ACS_CARD_NO_LEN];
    public byte[] byErrorMsg = new byte[HikConstant.ERROR_MSG_LEN];
    public int dwReaderNo;
    public byte byRecvStatus;
    public byte[] byRes = new byte[131];
}
