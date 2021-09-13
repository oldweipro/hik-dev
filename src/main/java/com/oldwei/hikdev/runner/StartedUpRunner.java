package com.oldwei.hikdev.runner;

import com.oldwei.hikdev.mqtt.MqttConnectClient;
import com.oldwei.hikdev.service.IUdpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @Description 实现 ApplicationRunner 接口后项目启动时会按照执行顺序执行 run 方法
 * @Author oldwei
 * @Date 2019/8/23 11:39
 * @Version 2.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StartedUpRunner implements ApplicationRunner {
    private final MqttConnectClient mqttConnectClient;
    private final IUdpService udpService;

    @Value("${mqtt.enable}")
    private boolean mqttEnable;

    @Override
    public void run(ApplicationArguments args) {
        if (mqttEnable) {
            //实例化bean之后 初始化连接
            this.mqttConnectClient.initMqttClient();
            this.mqttConnectClient.mqttConnect();
        }
        this.udpService.receive();
        log.info("=========================项目启动完成=========================");
    }
}
