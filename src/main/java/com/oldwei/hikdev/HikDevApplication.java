package com.oldwei.hikdev;

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
        SpringApplication.run(HikDevApplication.class, args);
    }

}
