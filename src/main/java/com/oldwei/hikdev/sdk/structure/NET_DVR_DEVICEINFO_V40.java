package com.oldwei.hikdev.sdk.structure;

import com.sun.jna.Structure;

/**
 * NET_DVR_Login_V40()参数
 *
 * @author oldwei
 * @date 2021-5-12 15:15
 */
public class NET_DVR_DEVICEINFO_V40 extends Structure {
    public NET_DVR_DEVICEINFO_V30 struDeviceV30 = new NET_DVR_DEVICEINFO_V30();
    public byte bySupportLock;
    public byte byRetryLoginTime;
    public byte byPasswordLevel;
    public byte byRes1;
    public int dwSurplusLockTime;
    /**
     * 字符编码类型：
     * 0- 无字符编码信息(老设备)，
     * 1- GB2312(简体中文)，
     * 2- GBK，
     * 3- BIG5(繁体中文)，
     * 4- Shift_JIS(日文)，
     * 5- EUC-KR(韩文)，
     * 6- UTF-8，
     * 7- ISO8859-1，
     * 8- ISO8859-2，
     * 9- ISO8859-3，
     * …，
     * 依次类推，
     * 21- ISO8859-15(西欧)
     */
    public byte byCharEncodeType;
    /**
     * 支持v50版本的设备参数获取，设备名称和设备类型名称长度扩展为64字节
     */
    public byte bySupportDev5;
    /**
     * 能力集扩展，位与结果：0- 不支持，1- 支持
     */
    public byte bySupport;
    /**
     * 登录模式 0-Private登录 1-ISAPI登录
     */
    public byte byLoginMode;
    public int dwOEMCode;
    /**
     * 该用户密码剩余有效天数，单位：天，返回负值，表示密码已经超期使用，例如“-3表示密码已经超期使用3天”
     */
    public int iResidualValidity;
    /**
     * iResidualValidity字段是否有效，0-无效，1-有效
     */
    public byte byResidualValidity;
    /**
     * 独立音轨接入的设备，起始接入通道号，0-为保留字节，无实际含义，音轨通道号不能从0开始
     */
    public byte bySingleStartDTalkChan;
    /**
     * 独立音轨接入的设备的通道总数，0-表示不支持
     */
    public byte bySingleDTalkChanNums;
    /**
     * 0-无效，1-管理员创建一个非管理员用户为其设置密码，该非管理员用户正确登录设备后要提示“请修改初始登录密码”，未修改的情况下，用户每次登入都会进行提醒；2-当非管理员用户的密码被管理员修改，该非管理员用户再次正确登录设备后，需要提示“请重新设置登录密码”，未修改的情况下，用户每次登入都会进行提醒。
     */
    public byte byPassWordResetLevel;
    /**
     * 能力集扩展，位与结果：0- 不支持，1- 支持 bySupportStreamEncrypt & 0x1:表示是否支持RTP/TLS取流 bySupportStreamEncrypt & 0x2:  表示是否支持SRTP/UDP取流 bySupportStreamEncrypt & 0x4:  表示是否支持SRTP/MULTICAST取流
     */
    public byte bySupportStreamEncrypt;
    /**
     * 0-无效（未知类型）,1-经销型，2-行业型
     */
    public byte byMarketType;
    public byte[] byRes2 = new byte[238];
}
