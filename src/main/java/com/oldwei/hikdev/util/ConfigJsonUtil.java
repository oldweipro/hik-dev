package com.oldwei.hikdev.util;

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
    public static JSONObject readConfigJson() {
        String filePath = System.getProperty("user.dir") + "/sdk/config/config.json";
        FileReader fileReader = new FileReader(filePath);
        JSONObject jsonObject = JSON.parseObject(fileReader.readString());
        if (ObjectUtil.isNull(jsonObject)) {
            jsonObject = new JSONObject();
        }
        return jsonObject;
    }

    public static boolean writeConfigJson(String json) {
        String filePath = System.getProperty("user.dir") + "/sdk//config/config.json";
        FileWriter writer = new FileWriter(filePath);
        try {
            writer.write(json);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
