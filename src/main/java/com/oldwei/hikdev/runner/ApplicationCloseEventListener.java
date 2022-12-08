package com.oldwei.hikdev.runner;

import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.system.SystemUtil;
import com.oldwei.hikdev.service.IHikDevService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;
import cn.hutool.system.OsInfo;

/**
 * @author oldwei
 * @date 2022/8/28 13:04
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationCloseEventListener implements ApplicationListener<ContextClosedEvent> {
    private final IHikDevService hikDevService;
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        OsInfo osInfo = SystemUtil.getOsInfo();
        if (osInfo.isWindows()) {
            RuntimeUtil.exec("cmd /c taskkill /f /im rtsp_server.exe");
        } else if (osInfo.isLinux()) {
            // 杀死rtsp进程，计划改为docker容器管理
        }
        //清除hik注册信息
        this.hikDevService.NET_DVR_Cleanup();
        log.info("项目已停止");
    }
}
