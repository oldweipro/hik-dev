package com.oldwei.hikdev.mqtt;

import com.oldwei.hikdev.sdk.constant.MqttConstant;
import com.oldwei.hikdev.sdk.service.IAccessControlService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.stereotype.Component;

/**
 * @author oldwei
 * @date 2021-5-10 14:47
 */
@Slf4j
@Component
public class MqttConnectClient {
    private final IAccessControlService accessControlService;

    private MqttClient client;

    public MqttConnectClient(final IAccessControlService accessControlService) {
        this.accessControlService = accessControlService;
        try {
            MemoryPersistence persistence = new MemoryPersistence();
            this.client = new MqttClient(MqttConstant.BROKER, MqttConstant.CLIENT_ID, persistence);
            this.client.setCallback(new OnMessageCallback(this));
        } catch (MqttException me) {
            me.printStackTrace();
            this.close();
        }
        //实例化bean的时候初始化连接
        this.mqttConnect();

    }

    public void mqttConnect() {
        try {
            log.info("Mqtt开始连接");
            // MQTT 连接选项
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setUserName("hik_mqtt_oooldwei");
            // 保留会话
            connOpts.setCleanSession(true);
            // 建立连接
            this.client.connect(connOpts);
            log.info("Mqtt 连接成功");
            this.subscribe(MqttConstant.SUB_TOPIC);
            log.info("Mqtt 订阅成功：{}", MqttConstant.SUB_TOPIC);
        } catch (MqttException me) {
            me.printStackTrace();
            this.mqttConnect();
        }

    }

    public void subscribe(String subTopic) {
        try {
            // 订阅
            this.client.subscribe(subTopic);
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
            message.setQos(MqttConstant.QOS);
            this.client.publish(MqttConstant.PUB_TOPIC, message);
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
            this.client.disconnect();
            log.info("Disconnected");
            this.client.close();
            System.exit(0);
        } catch (MqttException me) {
            me.printStackTrace();
        }
    }

}
