package com.oldwei.hikdev.util;

import lombok.Getter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author oldwei
 * @date 2021-6-25 00:53
 */
@Getter
public class DataCache implements Serializable {
    private static final long serialVersionUID = 1936076342035183560L;
    private final Map<String, Object> data = new HashMap<>();

    public void set(String key, Object value) {
        this.data.put(key, value);
    }

    public Object get(String key) {
        return this.data.get(key);
    }
}
