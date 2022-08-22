package com.oldwei.hikdev.structure;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 12:29
 */
public class NET_VCA_RULE_ALARM extends Structure {
    public int dwSize;
    public int dwRelativeTime;
    public int dwAbsTime;
    public NET_VCA_RULE_INFO struRuleInfo;
    public NET_VCA_TARGET_INFO struTargetInfo;
    public NET_VCA_DEV_INFO struDevInfo;
    public int dwPicDataLen;
    public byte byPicType;
    /**
     * 关联通道报警图片数量
     */
    public byte byRelAlarmPicNum;
    /**
     * IDS设备返回0(默认值)，Smart Functiom Return 1
     */
    public byte bySmart;
    /**
     * 图片数据传输方式: 0-二进制；1-url
     */
    public byte byPicTransType;
    /**
     * 报警ID，用以标识通道间关联产生的组合报警，0表示无效
     */
    public int dwAlarmID;
    /**
     * 与NET_VCA_DEV_INFO里的byIvmsChannel含义相同，能表示更大的值。老客户端用byIvmsChannel能继续兼容，但是最大到255。新客户端版本请使用wDevInfoIvmsChannelEx。
     */
    public short wDevInfoIvmsChannelEx;
    /**
     * dwRelativeTime字段是否有效  0-无效， 1-有效，dwRelativeTime表示UTC时间
     */
    public byte byRelativeTimeFlag;
    /**
     * 附加信息上传使能 0-不上传 1-上传
     */
    public byte byAppendInfoUploadEnabled;
    /**
     * 指向附加信息NET_VCA_APPEND_INFO的指针，byAppendInfoUploadEnabled为1时或者byTimeDiffFlag为1时有效
     */
    public Pointer pAppendInfo;
    public Pointer pImage;
}
