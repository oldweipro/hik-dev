package com.oldwei.hikdev.component;

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

    public boolean hasKey(String key) {
        return this.data.containsKey(key);
    }

    public Object get(String key) {
        return this.hasKey(key) ? this.data.get(key) : null;
    }

    public String getString(String key) {
        return this.hasKey(key) ? this.get(key).toString() : null;
    }

    public Integer getInteger(String key) {
        return this.hasKey(key) ? Integer.valueOf(this.getString(key)) : null;
    }

    public boolean removeKey(String key) {
        return this.data.keySet().removeIf(keys -> keys.contains(key));
    }
}
