package com.oldwei.hikdev.mqtt;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


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
        String payload = new String(message.getPayload());
        try {
            //防止消息内容不是json字符串，json转换异常导致程序崩溃
            JSONObject obj = JSONObject.parseObject(payload);
            Integer code = obj.getInteger("code");
            JSONObject data = obj.getJSONObject("data");
            if (ObjectUtil.isAllNotEmpty(code, data)) {
                // 根据消息业务进行业务分配
                ThreadUtil.execAsync(() -> {
                    HttpUtil.post("127.0.0.1:8923/mqttRequest", obj.toJSONString());
                });
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        log.info("deliveryComplete---------{}", token.isComplete());
    }
}
