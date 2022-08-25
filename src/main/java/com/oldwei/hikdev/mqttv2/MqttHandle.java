package com.oldwei.hikdev.mqttv2;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MqttHandle implements MqttCallback {
    /** 日志组件 */
    private static Logger log = LoggerFactory.getLogger(MqttHandle.class);

    /**
     * 连接丢失
     * @param cause
     */
    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("connection lost：" + cause.getMessage());
        MqttInitialized.reconnection();
    }

    /**
     * 收到消息
     * @param topic
     * @param message
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) {
        System.out.println("Received message: \n  topic：" + topic + "\n  Qos：" + message.getQos() + "\n  payload：" + new String(message.getPayload()));
    }

    /**
     * 消息传递成功
     * @param token
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        System.out.println("消息传递成功");
    }

}