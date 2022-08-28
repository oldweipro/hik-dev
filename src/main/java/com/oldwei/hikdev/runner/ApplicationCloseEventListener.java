package com.oldwei.hikdev.runner;

import cn.hutool.core.util.RuntimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

/**
 * @author oldwei
 * @date 2022/8/28 13:04
 */
@Slf4j
@Component
public class ApplicationCloseEventListener implements ApplicationListener<ContextClosedEvent> {
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        RuntimeUtil.exec("cmd /c taskkill /f /im rtsp_server.exe");
        log.info("项目已停止");
    }
}
