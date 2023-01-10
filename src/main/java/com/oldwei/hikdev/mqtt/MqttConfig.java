package com.oldwei.hikdev.mqtt;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class MqttConfig implements Serializable {

    private static final long serialVersionUID = 7820047650653436909L;

    private String broker = "topic/cloud", subTopic, pubTopic = "topic/hik", username = "hik", password = "dev";
    private Integer qos = 2;
}
