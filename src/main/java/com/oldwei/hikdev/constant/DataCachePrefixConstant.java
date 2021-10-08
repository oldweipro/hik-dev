package com.oldwei.hikdev.constant;

/**
 * @author oldwei
 * @date 2021-5-13 11:35
 */
public interface DataCachePrefixConstant {
    /**
     * 设备注册
     */
    String HIK_REG_USERID = "hik:reg:userid:sn:";
    /**
     * 设备预览
     */
    String HIK_PREVIEW_VIEW = "hik:preview:view:sn:";
    /**
     * 设备推流0：关闭 1：开启
     */
    String HIK_PUSH_STATUS = "hik:push:status:sn:";
    /**
     * 设备布防
     */
    String HIK_ALARM_HANDLE = "hik:alarm:handle:sn:";
    /**
     * 设备监听
     */
    String HIK_ALARM_LISTEN = "hik:alarm:listen:sn:";
    /**
     * 设备编码
     */
    String HIK_REG_CHAR_ENCODE_TYPE = "hik:reg:char:encode:type:sn:";
    /**
     * 设备拉流地址
     */
    String HIK_PUSH_PULL_STREAM_ADDRESS = "hik:push:pull:stream:address:sn:";
}
