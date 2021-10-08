package com.oldwei.hikdev.entity.param;

import lombok.Data;

import java.io.Serializable;

/**
 * @author oldwei
 * @date 2021-9-30 10:45
 */
@Data
public class AccessControlUser implements Serializable {
    private static final long serialVersionUID = 6771194369306848896L;
    /**
     * 设备序列号 唯一标识
     */
    private String deviceSn;
    /**
     * 姓名
     */
    private String realName;
    /**
     * 卡号
     */
    private String cardNo;
    /**
     * 工号
     */
    private Integer employeeNo;
    /**
     * 计划模板
     */
    private Short planTemplateNumber;
    /**
     * base64图片
     */
    private String base64Pic;
}
