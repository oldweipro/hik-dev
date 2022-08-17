package com.oldwei.hikdev.scheduled;

import com.oldwei.hikdev.util.ConfigJsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author oldwei
 * @date 2021-9-16 14:10
 */
@Slf4j
@Component
public class ScheduledTask {
    // Cron表达式范例：
    //
    //每隔5秒执行一次：*/5 * * * * ?
    //
    //每隔1分钟执行一次：0 */1 * * * ?
    //
    //每天23点执行一次：0 0 23 * * ?
    //
    //每天凌晨1点执行一次：0 0 1 * * ?
    //
    //每月1号凌晨1点执行一次：0 0 1 1 * ?
    //
    //每月最后一天23点执行一次：0 0 23 L * ?
    //
    //每周星期天凌晨1点实行一次：0 0 1 ? * L
    //
    //在26分、29分、33分执行一次：0 26,29,33 * * * ?
    //
    //每天的0点、13点、18点、21点都执行一次：0 0 0,13,18,21 * * ?

    /**
     * 一分钟执行一次
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void searchHikDevice() {
        ConfigJsonUtil.searchHikDevice();
    }

}
