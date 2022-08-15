package com.oldwei.hikdev.mqtt;

import com.alibaba.fastjson2.JSONObject;
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
        try {
            String payload = new String(message.getPayload());
            //防止消息内容不是json字符串，json转换异常导致程序崩溃
            JSONObject obj = JSONObject.parseObject(payload);
            log.info("收到mqtt订阅消息: {}", obj);
            // TODO 在这里继续写业务
        } catch (Exception ignored) {
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        log.info("deliveryComplete---------{}", token.isComplete());
    }
}
