package com.oldwei.hikdev.mqtt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;


/**
 * @author oldwei
 * @date 2021-5-10 12:28
 */
@Slf4j
@RequiredArgsConstructor
public class OnMessageCallback implements MqttCallback {
    private final MqttConnectClient mqttConnectClient;

    @Override
    public void connectionLost(Throwable cause) {
        // 连接丢失后，一般在这里面进行重连
        log.info("MQTT连接断开，正在重连");
        this.mqttConnectClient.mqttConnect();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        // subscribe后得到的消息会执行到这里面
        String command = new String(message.getPayload());
//        log.info("接收消息主题:{}", topic);
//        log.info("接收消息Qos:{}", message.getQos());
//        log.info("接收消息内容:{}", command);

        this.mqttConnectClient.commandMqtt(command);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        log.info("deliveryComplete---------{}", token.isComplete());
    }
}
