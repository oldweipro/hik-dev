package com.oldwei.hikdev.config;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.setting.Setting;
import cn.hutool.system.OsInfo;
import cn.hutool.system.SystemUtil;
import com.oldwei.hikdev.service.IHikDevService;
import com.oldwei.hikdev.service.IHikPlayCtrlService;
import com.oldwei.hikdev.component.DataCache;
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
        OsInfo osInfo = SystemUtil.getOsInfo();
        IHikDevService hikDevService = null;
        if (osInfo.isWindows()) {
            hikDevService = (IHikDevService) Native.loadLibrary(System.getProperty("user.dir") + "\\sdk\\windows\\HCNetSDK.dll", IHikDevService.class);
        } else if (osInfo.isLinux()) {
            hikDevService = (IHikDevService) Native.loadLibrary(System.getProperty("user.dir") + "\\sdk\\linux\\libhcnetsdk.so", IHikDevService.class);
        }
        assert hikDevService != null;
        hikDevService.NET_DVR_Init();
        return hikDevService;
    }

    @Bean
    public IHikPlayCtrlService hikPlayCtrlService() {
        return (IHikPlayCtrlService) Native.loadLibrary(System.getProperty("user.dir") + "\\sdk\\windows\\PlayCtrl.dll", IHikPlayCtrlService.class);
    }

    @Bean
    public DataCache hikMemory() {
        return new DataCache();
    }

    @Bean
    public Setting configSetting() {
        String property = System.getProperty("user.dir") + "\\sdk\\config\\config.setting";
        Setting setting = new Setting(FileUtil.touch(property), CharsetUtil.CHARSET_UTF_8, false);
        setting.autoLoad(true);
        return setting;
    }

}
