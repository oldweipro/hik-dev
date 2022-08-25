package com.oldwei.hikdev.mqttv2;

import com.oldwei.hikdev.config.MqttConfig;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class MqttInitialized {

    /** MQTT客户端 */
    private static MqttClient client = null;
    /** 连接选项 */
    private static MqttConnectOptions connOpts = null;
    /** 连接状态 */
    private static Boolean connectStatus = false;
    @Autowired
    private MqttConfig mqtt;
    private static MqttConfig MQTTCONFIG;
    @PostConstruct
    public void postConstruct(){
        MQTTCONFIG=mqtt;
    }
    /** 日志组件 */
    private static Logger log = LoggerFactory.getLogger(MqttInitialized.class);

    /**
     * 获取MQTT客户端连接状态
     * @return
     */
    public static Boolean getConnectStatus() {
        return connectStatus;
    }

    static {
        try {
            // MQTT 连接选项
            connOpts = new MqttConnectOptions();
            // 设置认证信息
            connOpts.setUserName(MQTTCONFIG.getUsername());
            connOpts.setPassword(MqttConfig.PASSWD.toCharArray());
            //  持久化
            MemoryPersistence persistence = new MemoryPersistence();
            // MQ客户端建立
            client = new MqttClient(MQTTCONFIG.getIp(), MqttClient.generateClientId(), persistence);
            // 设置回调
            client.setCallback(new MqttHandle());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * MQTT客户端启动
     */
    @PostConstruct
    public static void connect() {
        try {
            // 建立连接
            client.connect(connOpts);
            connectStatus = client.isConnected();
            log.info("MQTT服务器连接成功~~~");
        } catch (Exception e) {
            connectStatus = client.isConnected();
            log.error("MQTT服务器连接失败!!");
            e.printStackTrace();
            reconnection();
        }
    }

    /**
     * 消息订阅
     * @param topic 主题
     * @param qos   QOS
     */
    public void subscribe(String topic, Integer qos) throws MqttException {
        client.subscribe(topic, qos);
    }

    /**
     * 消息发布
     * @param topic   主题
     * @param message 消息体
     * @param qos     QOS
     */
    public void publish(String topic, String message, Integer qos) throws MqttException {
        MqttMessage mqttMessage = new MqttMessage(message.getBytes());
        mqttMessage.setQos(qos);
        client.publish(topic, mqttMessage);
    }

    /**
     * 断线重连
     */
    public static void reconnection() {
        // 尝试进行重新连接
        while (true) {
            if (MqttInitialized.getConnectStatus()) {
                // 查询连接状态 连接成功则停止重连
                break;
            }
            try {
                log.info("开始进行MQTT服务器连接.......");
                // 进行连接
                connect();
                Thread.sleep(10000);
            } catch (Exception e) {
                log.error("重新连接出现异常");
                e.printStackTrace();
                break;
            }
        }
    }

    /**
     * 测试方法 进行消息订阅
     */
    public void test() {
        try {
            subscribe(MQTTCONFIG.getTopic(), MQTTCONFIG.getQos());
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }
}