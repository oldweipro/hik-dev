package com.oldwei.hikdev.annotation.aspect;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.oldwei.hikdev.annotation.CheckDeviceLogin;
import com.oldwei.hikdev.component.DataCache;
import com.oldwei.hikdev.constant.DataCachePrefixConstant;
import com.oldwei.hikdev.entity.HikDevResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author oldwei
 * @date 2021-9-30 11:03
 */
@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class CheckDeviceLoginAspect extends BaseAspectSupport {
    private final DataCache dataCache;
    @Pointcut("execution(* com.oldwei.hikdev.controller.*.*(..)) && @annotation(com.oldwei.hikdev.annotation.CheckDeviceLogin)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        JSONObject parameter = resolveParameter(point);
        Method targetMethod = resolveMethod(point);
        CheckDeviceLogin annotation = targetMethod.getAnnotation(CheckDeviceLogin.class);
        String operation = annotation.operation();
        long start = System.currentTimeMillis();
        String deviceSn = parameter.getString("deviceSn");
        if (StrUtil.isNotBlank(deviceSn)) {
            Integer longUserId = this.dataCache.getInteger(DataCachePrefixConstant.HIK_REG_USERID + deviceSn);
            if (null == longUserId) {
                return new HikDevResponse().err("设备状态未注册");
            }
        }
        return point.proceed();
    }
}
