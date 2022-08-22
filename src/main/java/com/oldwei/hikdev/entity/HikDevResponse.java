package com.oldwei.hikdev.entity;

import cn.hutool.http.HttpStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

/**
 * 接口返回数据格式
 *
 * @author oldwei
 * @date 2021-9-24 17:13
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class HikDevResponse extends HashMap<String, Object> {
    private static final long serialVersionUID = 1048666748687265045L;

    public HikDevResponse code(int status) {
        this.put("code", status);
        return this;
    }

    public HikDevResponse msg(String msg) {
        this.put("msg", msg);
        return this;
    }

    public HikDevResponse data(Object data) {
        this.put("data", data);
        return this;
    }

    public HikDevResponse time() {
        this.put("timestamp", System.currentTimeMillis());
        return this;
    }

    public HikDevResponse ok() {
        this.code(HttpStatus.HTTP_OK);
        this.time();
        return this;
    }

    public HikDevResponse ok(String msg) {
        this.code(HttpStatus.HTTP_OK);
        this.time();
        this.msg(msg);
        return this;
    }

    public HikDevResponse ok(Map<String, Object> data) {
        this.code(HttpStatus.HTTP_OK);
        this.time();
        this.data(data);
        return this;
    }

    public HikDevResponse ok(String msg, Object data) {
        this.code(HttpStatus.HTTP_OK);
        this.time();
        this.msg(msg);
        this.data(data);
        return this;
    }

    public HikDevResponse err() {
        this.code(HttpStatus.HTTP_INTERNAL_ERROR);
        return this;
    }

    public HikDevResponse err(String msg) {
        this.code(HttpStatus.HTTP_INTERNAL_ERROR);
        this.time();
        this.msg(msg);
        return this;
    }

    public HikDevResponse err(Object data) {
        this.code(HttpStatus.HTTP_INTERNAL_ERROR);
        this.time();
        this.data(data);
        return this;
    }

    public HikDevResponse err(String msg, Object data) {
        this.code(HttpStatus.HTTP_INTERNAL_ERROR);
        this.time();
        this.msg(msg);
        this.data(data);
        return this;
    }


    @Override
    public HikDevResponse put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
