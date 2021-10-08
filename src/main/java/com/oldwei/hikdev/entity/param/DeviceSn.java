package com.oldwei.hikdev.entity.param;

import lombok.Data;

import java.io.Serializable;

/**
 * @author oldwei
 * @date 2021-9-30 10:42
 */
@Data
public class DeviceSn implements Serializable {
    private static final long serialVersionUID = 4345170525981682L;
    /**
     * 设备序列号
     */
    private String deviceSn;
}
