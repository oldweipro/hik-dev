package com.oldwei.hikdev.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author oldwei
 * @date 2022/8/23 9:36
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Value("${hik-dev.output-dir}")
    private String outputDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String locationPath = "file:" + System.getProperty("user.dir") + "\\" + outputDir + "\\";
        //自定义路径pic, addResourceLocations指定访问资源所在目录
        registry.addResourceHandler("/**").addResourceLocations(locationPath);
    }

}
