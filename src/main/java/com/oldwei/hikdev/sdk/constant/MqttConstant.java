package com.oldwei.hikdev.sdk.constant;

/**
 * @author oldwei
 * @date 2021-5-12 17:55
 */
public interface MqttConstant {
    String SUB_TOPIC = "topic/cloud";

    String PUB_TOPIC = "topic/hik";

    int QOS = 2;

    String BROKER = "tcp://127.0.0.1:1883";

    String CLIENT_ID = "hik_mqtt_server";
}
