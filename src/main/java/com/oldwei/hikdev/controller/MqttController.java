package com.oldwei.hikdev.controller;

import com.oldwei.hikdev.entity.HikDevResponse;
import com.oldwei.hikdev.mqtt.MqttConnectClient;
import com.oldwei.hikdev.service.IHikAlarmDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("mqtt")
@RequiredArgsConstructor
public class MqttController {
    private final MqttConnectClient mqttConnectClient;
    private final IHikAlarmDataService hikAlarmDataService;

    @GetMapping("init")
    public HikDevResponse initMqttClient(String broker, String subTopic, String pubTopic, Integer qos, String username, String password) {

        this.mqttConnectClient.setBroker(broker);
        this.mqttConnectClient.setSubTopic(subTopic);
        this.mqttConnectClient.setPubTopic(pubTopic);
        this.mqttConnectClient.setQos(qos);
        this.mqttConnectClient.setUsername(username);
        this.mqttConnectClient.setPassword(password);
        this.mqttConnectClient.initMqttClient();
        this.mqttConnectClient.mqttConnect();
        return new HikDevResponse().ok("init完成✅");
    }

    @GetMapping("setProjectId")
    public HikDevResponse setProjectId(String projectId) {
        this.hikAlarmDataService.setProjectId(projectId);
        return new HikDevResponse().ok("设置项目ID完成✅");
    }
}
