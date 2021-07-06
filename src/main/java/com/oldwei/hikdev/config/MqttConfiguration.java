package com.oldwei.hikdev.config;

import com.oldwei.hikdev.mqtt.MqttConnectClient;
import com.oldwei.hikdev.service.IAccessControlService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author oldwei
 * @date 2021-6-25 02:18
 */
@Configuration
@RequiredArgsConstructor
public class MqttConfiguration {
    private final IAccessControlService accessControlService;

    @Bean
    public MqttConnectClient mqttConnectClient() {
        return new MqttConnectClient(this.accessControlService);
    }

}
