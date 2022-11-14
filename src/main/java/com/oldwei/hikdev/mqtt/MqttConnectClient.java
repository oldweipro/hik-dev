package com.oldwei.hikdev.mqtt;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author oldwei
 * @date 2021-5-10 14:47
 */
@Slf4j
@Component
public class MqttConnectClient {

    @Value("${mqtt.settings.sub-topic}")
    private String subTopic;
    @Value("${mqtt.settings.pub-topic}")
    private String pubTopic;
    @Value("${mqtt.settings.qos}")
    private Integer qos;
    @Value("${mqtt.settings.username}")
    private String username;
    @Value("${mqtt.settings.password}")
    private String password;
    @Value("${mqtt.settings.broker}")
    private String broker;

    private MqttClient mqttClient;

    public void initMqttClient() {
        try {
            this.mqttClient = new MqttClient(broker, RandomUtil.randomString(16), new MemoryPersistence());
            //设置回调
            this.mqttClient.setCallback(new OnMessageCallback(this));
        } catch (MqttException e) {
            log.info("Mqtt初始化失败");
        }
    }

    public void mqttConnect() {
        try {
            Thread.sleep(5 * 1000L);
            log.info("Mqtt开始连接");
            // MQTT 连接选项
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setUserName(username);
            connOpts.setPassword(password.toCharArray());
            // 保留会话
            connOpts.setCleanSession(true);
            // 建立连接
            this.mqttClient.connect(connOpts);
            log.info("Mqtt 连接成功");
            this.subscribe(subTopic);
            log.info("Mqtt 订阅成功：{}", subTopic);
        } catch (MqttException me) {
            log.error("连接mqtt异常,重新连接。");
            this.mqttConnect();
        } catch (InterruptedException e) {
            log.error("连接mqtt,睡眠异常");
        }

    }

    public void subscribe(String subTopic) {
        try {
            // 订阅
            this.mqttClient.subscribe(subTopic);
        } catch (MqttException me) {
            me.printStackTrace();
        }
    }

    /**
     * 发布消息 消息发布所需参数
     */
    public void publish(String content) {
        try {
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            this.mqttClient.publish(pubTopic, message);
//            log.info("Message published");
        } catch (MqttException me) {
            me.printStackTrace();
        }
    }

    public void close() {
        try {
            this.mqttClient.disconnect();
//            log.info("Mqtt Disconnected");
            this.mqttClient.close();
//            log.info("Mqtt Closed");
        } catch (MqttException me) {
            me.printStackTrace();
        }
    }

}
