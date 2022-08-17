package com.oldwei.hikdev.entity.config;

import lombok.Data;

import java.io.Serializable;

/**
 * @author oldwei
 * @date 2022/8/17 13:29
 */
@Data
public class ConfigJson implements Serializable {
    private static final long serialVersionUID = 273135886148788073L;
    private DeviceLoginDTO deviceLogin;
    private DeviceSearchInfo deviceSearchInfo;
}
