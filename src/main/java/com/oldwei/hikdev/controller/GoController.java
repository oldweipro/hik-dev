package com.oldwei.hikdev.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author oldwei
 * @date 2021-7-13 11:37
 */
@RestController
public class GoController {

    /**
     * 天气列表
     * @param flag
     * @return
     */
    @GetMapping("warningData/list")
    public JSONObject warningDataList(Integer flag) {
        return reqGet("warningData/list?flag=" + flag);
    }
    /**
     * 人流量分析
     * @param flag
     * @return
     */
    @GetMapping("getStatistics")
    public JSONObject getStatistics(Integer flag) {
        return reqGet("getStatistics?flag=" + flag);
    }

    private JSONObject reqGet(String uri) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://139.129.119.218:2333/" + uri;
        String body = restTemplate.getForEntity(url, String.class).getBody();
        return JSONObject.parseObject(body);
    }
}
