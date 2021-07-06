package com.oldwei.hikdev.structure;

import com.oldwei.hikdev.constant.HikConstant;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-14 12:07
 */
public class NET_DVR_FACE_COND extends Structure {
    public int dwSize;
    public byte[] byCardNo = new byte[HikConstant.ACS_CARD_NO_LEN];
    public int dwFaceNum;
    public int dwEnableReaderNo;
    public byte[] byRes = new byte[124];
}
