package com.oldwei.hikdev.thread;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Set;

/**
 * @Description 继承 ApplicationRunner 接口后项目启动时会按照执行顺序执行 run 方法
 * @Author oldwei
 * @Date 2019/8/23 11:39
 * @Version 2.0
 */
@Component
@Order(value = 1)
@Slf4j
@RequiredArgsConstructor
public class ThreadTask implements ApplicationRunner {

    private final RedisTemplate<String, Serializable> redisTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //项目启动时清空hik*数据
        Set<String> keys = redisTemplate.keys("hik*");
        if (null != keys && keys.size() > 0) {
            this.redisTemplate.delete(keys);
        }
        log.info("=========================项目启动完成=========================");
    }
}
