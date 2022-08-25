package com.oldwei.hikdev.entity.config;

import lombok.Data;

import java.io.Serializable;

/**
 * @author oldwei
 * @date 2022/8/25 10:26
 */
@Data
public class DeviceChannel implements Serializable {

    private static final long serialVersionUID = 3878827502853787853L;

    /**
     * 通道号
     */
    private Integer channelId;
    /**
     * 该通道是否启用，IP通道在线状态，是一个只读的属性；0表示HDVR或者NVR设备的数字通道连接对应的IP设备失败，该通道不在线；1表示连接成功，该通道在线
     */
    private Integer byEnable;
}
