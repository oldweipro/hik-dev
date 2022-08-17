package com.oldwei.hikdev.entity.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author oldwei
 * @date 2022/8/16 16:16
 */
@Setter
@Getter
public class DeviceSearchInfo implements Serializable {
    private static final long serialVersionUID = 6278149525617953088L;
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
    private String charEncodeType;
    private Integer loginId;
    private String username;
    private String password;
    private String title;
    private Boolean useAsync = false;

    public void setDeviceSearchInfoDTO(DeviceSearchInfoDTO deviceSearchInfoDTO) {
        this.Types = deviceSearchInfoDTO.getTypes();
        this.DeviceDescription = deviceSearchInfoDTO.getDeviceDescription();
        this.HCPlatformEnable = deviceSearchInfoDTO.getHCPlatformEnable();
        this.CommandPort = deviceSearchInfoDTO.getCommandPort();
        this.DigitalChannelNum = deviceSearchInfoDTO.getDigitalChannelNum();
        this.SupportHCPlatform = deviceSearchInfoDTO.getSupportHCPlatform();
        this.DSPVersion = deviceSearchInfoDTO.getDSPVersion();
        this.IPv4Address = deviceSearchInfoDTO.getIPv4Address();
        this.Salt = deviceSearchInfoDTO.getSalt();
        this.Activated = deviceSearchInfoDTO.getActivated();
        this.AnalogChannelNum = deviceSearchInfoDTO.getAnalogChannelNum();
        this.supportIPv6 = deviceSearchInfoDTO.getSupportIPv6();
        this.PasswordResetAbility = deviceSearchInfoDTO.getPasswordResetAbility();
        this.supportModifyIPv6 = deviceSearchInfoDTO.getSupportModifyIPv6();
        this.DeviceLock = deviceSearchInfoDTO.getDeviceLock();
        this.HttpPort = deviceSearchInfoDTO.getHttpPort();
        this.DHCPAbility = deviceSearchInfoDTO.getDHCPAbility();
        this.DeviceSN = deviceSearchInfoDTO.getDeviceSN();
        this.BootTime = deviceSearchInfoDTO.getBootTime();
        this.MAC = deviceSearchInfoDTO.getMAC();
        this.DHCP = deviceSearchInfoDTO.getDHCP();
        this.Encrypt = deviceSearchInfoDTO.getEncrypt();
        this.OEMInfo = deviceSearchInfoDTO.getOEMInfo();
        this.IPv4SubnetMask = deviceSearchInfoDTO.getIPv4SubnetMask();
        this.DeviceType = deviceSearchInfoDTO.getDeviceType();
        this.Uuid = deviceSearchInfoDTO.getUuid();
        this.SoftwareVersion = deviceSearchInfoDTO.getSoftwareVersion();
        this.EHomeVer = deviceSearchInfoDTO.getEHomeVer();
        this.IPv4Gateway = deviceSearchInfoDTO.getIPv4Gateway();
        this.supportEzvizUnbind = deviceSearchInfoDTO.getSupportEzvizUnbind();
    }

    public void setDeviceLoginDTO(DeviceLoginDTO deviceLoginDTO) {
        this.username = deviceLoginDTO.getUsername();
        this.charEncodeType = deviceLoginDTO.getCharEncodeType();
        this.title = deviceLoginDTO.getTitle();
        this.password = deviceLoginDTO.getPassword();
        this.loginId = deviceLoginDTO.getLoginId();
        this.IPv4Address = deviceLoginDTO.getIpv4Address();
        this.CommandPort = deviceLoginDTO.getCommandPort();
    }

    /**
     * 这里的命名不能写get，否则，在json序列化时会序列化出多余字段，所以以find命名
     * @return
     */
    public DeviceLoginDTO findDeviceLoginDTO() {
        DeviceLoginDTO deviceLoginDTO = new DeviceLoginDTO();
        deviceLoginDTO.setUsername(this.username);
        deviceLoginDTO.setCharEncodeType(this.charEncodeType);
        deviceLoginDTO.setTitle(this.title);
        deviceLoginDTO.setPassword(this.password);
        deviceLoginDTO.setLoginId(this.loginId);
        deviceLoginDTO.setIpv4Address(this.IPv4Address);
        deviceLoginDTO.setCommandPort(this.CommandPort);
        return deviceLoginDTO;
    }
}
