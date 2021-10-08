package com.oldwei.hikdev.annotation.aspect;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author oldwei
 * @date 2021-9-30 11:09
 */
@Slf4j
public abstract class BaseAspectSupport {

    JSONObject resolveParameter(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Object[] args = point.getArgs();
        String[] parameterNames = signature.getParameterNames();
        JSONObject parameter = new JSONObject();
        for (int i = 0; i < parameterNames.length; i++) {
            if(i >= args.length){
                break;
            }
            Object value = args[i];
            String name = parameterNames[i];
            if (!(value instanceof HttpServletRequest) && !(value instanceof HttpServletResponse)) {
                parameter.put(name, value);
            }
        }
        return parameter;
    }
    Method resolveMethod(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Class<?> targetClass = point.getTarget().getClass();

        Method method = getDeclaredMethod(targetClass, signature.getName(),
                signature.getMethod().getParameterTypes());
        if (method == null) {
            throw new IllegalStateException("无法解析目标方法: " + signature.getMethod().getName());
        }
        return method;
    }

    private Method getDeclaredMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
        try {
            return clazz.getDeclaredMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
                return getDeclaredMethod(superClass, name, parameterTypes);
            }
        }
        return null;
    }
}
