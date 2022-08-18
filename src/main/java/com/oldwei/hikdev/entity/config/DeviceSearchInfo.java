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
    private String types;
    private String deviceDescription;
    private String hcPlatformEnable;
    private String commandPort;
    private Integer digitalChannelNum;
    private String supportHcPlatform;
    private String dspVersion;
    private String ipv4Address;
    private String salt;
    private String activated;
    private Integer analogChannelNum;
    private String supportIpv6;
    private String passwordResetAbility;
    private String supportModifyIpv6;
    private String deviceLock;
    private String httpPort;
    private String dhcpAbility;
    private String deviceSn;
    private String bootTime;
    private String mac;
    private String dhcp;
    private String encrypt;
    private String oemInfo;
    private String ipv4SubnetMask;
    private String deviceType;
    private String uuid;
    private String softwareVersion;
    private String ehomeVer;
    private String ipv4Gateway;
    private String supportEzvizUnbind;
    private String charEncodeType = "6";
    /**
     * 登录状态
     */
    private Integer loginId = -1;
    private String username;
    private String password;
    private String title;
    private Boolean useAsync = false;
    /**
     * 布防状态
     */
    private Integer alarmHandleId = -1;
    /**
     * 设备开启预览状态
     */
    private Integer PreviewHandle = -1;

    public void setDeviceSearchInfoDTO(DeviceSearchInfoDTO deviceSearchInfoDTO) {
        this.types = deviceSearchInfoDTO.getTypes();
        this.deviceDescription = deviceSearchInfoDTO.getDeviceDescription();
        this.hcPlatformEnable = deviceSearchInfoDTO.getHCPlatformEnable();
        this.commandPort = deviceSearchInfoDTO.getCommandPort();
        this.digitalChannelNum = deviceSearchInfoDTO.getDigitalChannelNum();
        this.supportHcPlatform = deviceSearchInfoDTO.getSupportHCPlatform();
        this.dspVersion = deviceSearchInfoDTO.getDSPVersion();
        this.ipv4Address = deviceSearchInfoDTO.getIPv4Address();
        this.salt = deviceSearchInfoDTO.getSalt();
        this.activated = deviceSearchInfoDTO.getActivated();
        this.analogChannelNum = deviceSearchInfoDTO.getAnalogChannelNum();
        this.supportIpv6 = deviceSearchInfoDTO.getSupportIPv6();
        this.passwordResetAbility = deviceSearchInfoDTO.getPasswordResetAbility();
        this.supportModifyIpv6 = deviceSearchInfoDTO.getSupportModifyIPv6();
        this.deviceLock = deviceSearchInfoDTO.getDeviceLock();
        this.httpPort = deviceSearchInfoDTO.getHttpPort();
        this.dhcpAbility = deviceSearchInfoDTO.getDHCPAbility();
        this.deviceSn = deviceSearchInfoDTO.getDeviceSN();
        this.bootTime = deviceSearchInfoDTO.getBootTime();
        this.mac = deviceSearchInfoDTO.getMAC();
        this.dhcp = deviceSearchInfoDTO.getDHCP();
        this.encrypt = deviceSearchInfoDTO.getEncrypt();
        this.oemInfo = deviceSearchInfoDTO.getOEMInfo();
        this.ipv4SubnetMask = deviceSearchInfoDTO.getIPv4SubnetMask();
        this.deviceType = deviceSearchInfoDTO.getDeviceType();
        this.uuid = deviceSearchInfoDTO.getUuid();
        this.softwareVersion = deviceSearchInfoDTO.getSoftwareVersion();
        this.ehomeVer = deviceSearchInfoDTO.getEHomeVer();
        this.ipv4Gateway = deviceSearchInfoDTO.getIPv4Gateway();
        this.supportEzvizUnbind = deviceSearchInfoDTO.getSupportEzvizUnbind();
    }

    public void setDeviceLoginDTO(DeviceLoginDTO deviceLoginDTO) {
        this.username = deviceLoginDTO.getUsername();
        this.charEncodeType = deviceLoginDTO.getCharEncodeType();
        this.title = deviceLoginDTO.getTitle();
        this.password = deviceLoginDTO.getPassword();
        this.loginId = deviceLoginDTO.getLoginId();
        this.ipv4Address = deviceLoginDTO.getIpv4Address();
        this.commandPort = deviceLoginDTO.getCommandPort();
    }

    public void setDeviceAlarmHandleDTO(DeviceAlarmHandleDTO deviceAlarmHandleDTO) {
        this.alarmHandleId = deviceAlarmHandleDTO.getAlarmHandleId();
        this.ipv4Address = deviceAlarmHandleDTO.getIpv4Address();
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
        deviceLoginDTO.setIpv4Address(this.ipv4Address);
        deviceLoginDTO.setCommandPort(this.commandPort);
        return deviceLoginDTO;
    }
}
