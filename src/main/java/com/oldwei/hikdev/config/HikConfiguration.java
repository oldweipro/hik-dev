package com.oldwei.hikdev.config;

import com.oldwei.hikdev.sdk.service.IHikDevService;
import com.oldwei.hikdev.sdk.service.IHikPlayCtrlService;
import com.sun.jna.Native;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author oldwei
 * @date 2021-5-13 15:06
 */
@Configuration
public class HikConfiguration {

    @Bean
    public IHikDevService hikDevService() {
        IHikDevService hikDevService = (IHikDevService) Native.loadLibrary(System.getProperty("user.dir") + "\\sdk\\HCNetSDK.dll", IHikDevService.class);
        hikDevService.NET_DVR_Init();
        return hikDevService;
    }

    @Bean
    public IHikPlayCtrlService hikPlayCtrlService() {
        return (IHikPlayCtrlService) Native.loadLibrary(System.getProperty("user.dir") + "\\sdk\\PlayCtrl.dll", IHikPlayCtrlService.class);
    }

}
