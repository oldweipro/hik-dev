package com.oldwei.hikdev.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

/**
 * @author oldwei
 * @date 2022/8/12 23:44
 */
public class ConfigJsonUtil {
    static final String configPath = System.getProperty("user.dir") + "/sdk/config/config.json";

    public static JSONObject readConfigJson() {
        boolean exist = FileUtil.exist(configPath);
        JSONObject jsonObject = new JSONObject();
        if (exist) {
            FileReader fileReader = new FileReader(configPath);
            jsonObject = JSON.parseObject(fileReader.readString());
            if (ObjectUtil.isNull(jsonObject)) {
                jsonObject = new JSONObject();
            }
        }
        return jsonObject;
    }

    public static boolean writeConfigJson(String json) {
        boolean exist = FileUtil.exist(configPath);
        if (!exist) {
            FileUtil.touch(configPath);
        }
        FileWriter writer = new FileWriter(configPath);
        try {
            writer.write(json);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
