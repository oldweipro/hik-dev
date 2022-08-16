package com.oldwei.hikdev.entity.device;

import lombok.Data;

import java.io.Serializable;

/**
 * @author oldwei
 * @date 2022/8/16 16:16
 */
@Data
public class DeviceSearchInfo implements Serializable {
    private String Types;
    private String DeviceDescription;
    private String HCPlatformEnable;
    private String CommandPort;
    private String DigitalChannelNum;
    private String SupportHCPlatform;
    private String DSPVersion;
    private String IPv4Address;
    private String Salt;
    private String Activated;
    private String AnalogChannelNum;
    private String supportIPv6;
    private String PasswordResetAbility;
    private String supportModifyIPv6;
    private String DeviceLock;
    private String HttpPort;
    private String DHCPAbility;
    private String DeviceSN;
    private String BootTime;
    private String MAC;
    private String DHCP;
    private String Encrypt;
    private String OEMInfo;
    private String IPv4SubnetMask;
    private String DeviceType;
    private String Uuid;
    private String SoftwareVersion;
    private String EHomeVer;
    private String IPv4Gateway;
    private String supportEzvizUnbind;
}
