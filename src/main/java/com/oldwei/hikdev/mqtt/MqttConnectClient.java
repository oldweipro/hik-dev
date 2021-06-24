package com.oldwei.hikdev.mqtt;

import com.oldwei.hikdev.sdk.service.IAccessControlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author oldwei
 * @date 2021-5-10 14:47
 */
@Slf4j
@RequiredArgsConstructor
public class MqttConnectClient {

    @Value("${mqtt.settings.sub-topic}")
    private String subTopic;
    @Value("${mqtt.settings.pub-topic}")
    private String pubTopic;
    @Value("${mqtt.settings.qos}")
    private Integer qos;
    @Value("${mqtt.settings.username}")
    private String username;
    @Value("${mqtt.settings.broker}")
    private String broker;
    @Value("${mqtt.settings.client-id}")
    private String clientId;
    private final IAccessControlService accessControlService;

    private MqttClient mqttClient;

    public void initMqttClient() {
        try {
            this.mqttClient = new MqttClient(broker, clientId, new MemoryPersistence());
            //设置回调
            this.mqttClient.setCallback(new OnMessageCallback(this));
        } catch (MqttException e) {
            log.info("Mqtt初始化失败");
        }
    }

    public void mqttConnect() {
        try {
            log.info("Mqtt开始连接");
            // MQTT 连接选项
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setUserName(username);
            // 保留会话
            connOpts.setCleanSession(true);
            // 建立连接
            this.mqttClient.connect(connOpts);
            log.info("Mqtt 连接成功");
            this.subscribe(subTopic);
            log.info("Mqtt 订阅成功：{}", subTopic);
        } catch (MqttException me) {
            me.printStackTrace();
            this.mqttConnect();
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
            log.info("Message published");
        } catch (MqttException me) {
            me.printStackTrace();
        }
    }

    public void commandMqtt(String command) {
        String result = this.accessControlService.commandMqtt(command);
        this.publish(result);
    }

    public void close() {
        try {
            this.mqttClient.disconnect();
            log.info("Disconnected");
            this.mqttClient.close();
            System.exit(0);
        } catch (MqttException me) {
            me.printStackTrace();
        }
    }

}
