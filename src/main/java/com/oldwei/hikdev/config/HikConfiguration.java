package com.oldwei.hikdev.config;

import cn.hutool.system.OsInfo;
import cn.hutool.system.SystemUtil;
import com.oldwei.hikdev.service.IHikDevService;
import com.oldwei.hikdev.service.IHikPlayCtrlService;
import com.sun.jna.Native;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author oldwei
 * @date 2021-5-13 15:06
 */
@Configuration
public class HikConfiguration {

    /**
     * 根据当前操作系统初始化海康sdk
     *
     * @return
     */
    @Bean
    public IHikDevService hikDevService() {
        OsInfo osInfo = SystemUtil.getOsInfo();
        IHikDevService hikDevService = null;
        if (osInfo.isWindows()) {
            hikDevService = (IHikDevService) Native.loadLibrary(System.getProperty("user.dir") + "\\sdk\\hik_sdk_windows\\HCNetSDK.dll", IHikDevService.class);
        } else if (osInfo.isLinux()) {
            hikDevService = (IHikDevService) Native.loadLibrary(System.getProperty("user.dir") + "\\sdk\\hik_sdk_linux\\libhcnetsdk.so", IHikDevService.class);
        }
        assert hikDevService != null;
        hikDevService.NET_DVR_Init();
        return hikDevService;
    }

    /**
     * 初始化播放插件sdk
     *
     * @return
     */
    @Bean
    public IHikPlayCtrlService hikPlayCtrlService() {
        OsInfo osInfo = SystemUtil.getOsInfo();
        IHikPlayCtrlService hikPlayCtrlService = null;
        if (osInfo.isWindows()) {
            hikPlayCtrlService = (IHikPlayCtrlService) Native.loadLibrary(System.getProperty("user.dir") + "\\sdk\\hik_sdk_windows\\PlayCtrl.dll", IHikPlayCtrlService.class);
        } else if (osInfo.isLinux()) {
            hikPlayCtrlService = (IHikPlayCtrlService) Native.loadLibrary(System.getProperty("user.dir") + "\\sdk\\hik_sdk_windows\\libPlayCtrl.so", IHikPlayCtrlService.class);
        }
        return hikPlayCtrlService;
    }

}
