package com.oldwei.hikdev.sdk.structure;

import com.oldwei.hikdev.sdk.service.HCNetSDK;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 13:18
 */
public class NET_ITS_PICTURE_INFO extends Structure {
    public int dwDataLen;
    public byte byType;
    public byte byDataType;
    public byte byCloseUpType;
    public byte byPicRecogMode;
    public int dwRedLightTime;
    public byte[] byAbsTime = new byte[32];
    public HCNetSDK.NET_VCA_RECT struPlateRect = new HCNetSDK.NET_VCA_RECT();
    public HCNetSDK.NET_VCA_RECT struPlateRecgRect = new HCNetSDK.NET_VCA_RECT();
    public Pointer pBuffer;
    /**
     * UTC时间
     */
    public int dwUTCTime;
    /**
     * 兼容能力字段，按位表示，值：0- 无效，1- 有效
     */
    public byte byCompatibleAblity;
    /**
     * 时差字段是否有效  0-时差无效， 1-时差有效
     */
    public byte byTimeDiffFlag;
    /**
     * 与UTC的时差（小时），-12 ... +14， +表示东区,，byTimeDiffFlag为1时有效
     */
    public byte cTimeDifferenceH;
    /**
     * 与UTC的时差（分钟），-30, 30, 45， +表示东区，byTimeDiffFlag为1时有效
     */
    public byte cTimeDifferenceM;
    public byte[] byRes2 = new byte[4];
}
