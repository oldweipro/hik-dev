package com.oldwei.hikdev.entity.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 为了接收post json传入的参数
 * @author oldwei
 * @date 2021-9-30 10:42
 */
@Data
public class DeviceIp implements Serializable {
    private static final long serialVersionUID = 4345170525981682L;
    /**
     * 设备IP
     */
    private String ipv4Address;
}
