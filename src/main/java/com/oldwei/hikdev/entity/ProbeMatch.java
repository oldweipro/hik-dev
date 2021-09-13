package com.oldwei.hikdev.entity;

import lombok.Data;

/**
 * 设备搜索返回的字段（可能有缺失）
 *
 * @author oldwei
 * @date 2021-9-12 22:37
 */
@Data
public class ProbeMatch {
    private String Uuid;
    private String Types;
    private String DeviceType;
    private String DeviceDescription;
    private String DeviceSN;
    private String CommandPort;
    private String HttpPort;
    private String MAC;
    private String IPv4Address;
    private String IPv4SubnetMask;
    private String IPv4Gateway;
    private String IPv6Address;
    private String IPv6Gateway;
    private String IPv6MaskLen;
    private String DHCP;
    private String AnalogChannelNum;
    private String DigitalChannelNum;
    private String SoftwareVersion;
    private String DSPVersion;
    private String BootTime;
    private String Encrypt;
    private String ResetAbility;
    private String DiskNumber;
    private String Activated;
    private String PasswordResetAbility;
    private String PasswordResetModeSecond;
    private String SupportSecurityQuestion;
    private String SupportHCPlatform;
    private String HCPlatformEnable;
    private String IsModifyVerificationCode;
    private String Salt;
    private String DeviceLock;
    private String SupportMailBox;
    private String supportEzvizUnbind;
}
