package com.oldwei.hikdev;

import com.oldwei.hikdev.util.RtspServerUtil;
import com.oldwei.hikdev.util.SdkUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author oldwei
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
public class HikDevApplication {

    public static void main(String[] args) {
        SdkUtil.initSdk();
        RtspServerUtil.initRtspServer();
        SpringApplication.run(HikDevApplication.class, args);
    }
}
