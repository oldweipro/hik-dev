package com.oldwei.hikdev.sdk.structure;

import com.oldwei.hikdev.constant.HikConstant;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 13:39
 */
public class NET_DVR_ACS_EVENT_INFO extends Structure {
    public int dwSize;
    public byte[] byCardNo = new byte[32];
    public byte byCardType;
    public byte byAllowListNo;
    public byte byReportChannel;
    public byte byCardReaderKind;
    public int dwCardReaderNo;
    public int dwDoorNo;
    public int dwVerifyNo;
    public int dwAlarmInNo;
    public int dwAlarmOutNo;
    public int dwCaseSensorNo;
    public int dwRs485No;
    public int dwMultiCardGroupNo;
    public short wAccessChannel;
    public byte byDeviceNo;
    public byte byDistractControlNo;
    public int dwEmployeeNo;
    public short wLocalControllerID;
    public byte byInternetAccess;
    public byte byType;
    public byte[] byMACAddr = new byte[HikConstant.MACADDR_LEN]; //物理地址，为0无效
    public byte bySwipeCardType;//刷卡类型，0-无效，1-二维码
    public byte byMask; //是否带口罩：0-保留，1-未知，2-不戴口罩，3-戴口罩
    public int dwSerialNo; //事件流水号，为0无效
    public byte byChannelControllerID; //通道控制器ID，为0无效，1-主通道控制器，2-从通道控制器
    public byte byChannelControllerLampID; //通道控制器灯板ID，为0无效（有效范围1-255）
    public byte byChannelControllerIRAdaptorID; //通道控制器红外转接板ID，为0无效（有效范围1-255）
    public byte byChannelControllerIREmitterID; //通道控制器红外对射ID，为0无效（有效范围1-255）
    public byte byHelmet;//可选，是否戴安全帽：0-保留，1-未知，2-不戴安全, 3-戴安全帽
    public byte[] byRes = new byte[3];
}
