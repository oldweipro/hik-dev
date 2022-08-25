package com.oldwei.hikdev.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
//@ConfigurationProperties(prefix = "mqtt")
public class MqttConfig {
    /** 订阅消息主题 - 可为多个 现指一个作为示例 */
    @Value("${mqtt.settings.sub-topic}")
    public String topic;
    //public static final String TOPIC = "topic/cloud";
    /** QOS */
    @Value("${mqtt.settings.qos}")
    public Integer qos;
  //  public static final Integer QOS = 2;
    /** 链接地址 */
    @Value("${mqtt.settings.broker}")
    public String ip;
   // public static final String IP_ADDRESS = "tcp://broker.emqx.io:1883";
    /** 用户名 */
    @Value("${mqtt.settings.username}")
    public String username;
   // public static final String USERNAME = "hik_mqtt_server";
    /** 密码 */
    public static final String PASSWD = "";
}
