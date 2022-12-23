package com.oldwei.hikdev.runner;

import com.oldwei.hikdev.service.IHikDevService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ApplicationCloseEventListener implements ApplicationListener<ContextClosedEvent> {
    private final IHikDevService hikDevService;
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        //清除hik注册信息
        this.hikDevService.NET_DVR_Cleanup();
        log.info("项目已停止");
    }
}
