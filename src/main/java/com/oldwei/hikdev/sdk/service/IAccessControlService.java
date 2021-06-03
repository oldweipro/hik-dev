package com.oldwei.hikdev.sdk.service;

/**
 * 门禁
 *
 * @author oldwei
 * @date 2021-5-12 17:59
 */
public interface IAccessControlService {

    /**
     * 接收MQTT传输的指令进行相关操作
     *
     * @param command 传入的指令
     * @return json字符串
     */
    String commandMqtt(String command);
}
