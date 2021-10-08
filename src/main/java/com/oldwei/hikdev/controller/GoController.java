package com.oldwei.hikdev.controller;

import com.alibaba.fastjson.JSONObject;
import com.oldwei.hikdev.entity.Device;
import com.oldwei.hikdev.service.IHikAlarmDataService;
import com.oldwei.hikdev.service.IHikDeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author oldwei
 * @date 2021-7-13 11:37
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class GoController {

    private final IHikDeviceService hikDeviceService;
    private final IHikAlarmDataService hikAlarmDataService;

    /**
     * 天气列表
     *
     * @param flag
     * @return
     */
    @GetMapping("warningData/list")
    public JSONObject warningDataList(Integer flag) {
        return reqGet("warningData/list?flag=" + flag);
    }

    /**
     * 人流量分析
     *
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

    @PostMapping("mqttRequest")
    public void mqttRequest(@RequestBody JSONObject obj) {
//        根据自定义消息体对消息进行解析
//        code:
//        1000: 登录
//        1001: 退出
//        1002: 布防
//        1003: 撤防
        Integer code = obj.getInteger("code");
        switch (code) {
            case 1000:
                this.hikDeviceService.login(obj.getObject("data", Device.class));
                break;
            case 1001:
                this.hikDeviceService.clean(obj.getJSONObject("data").getString("deviceSn"));
                break;
            case 1002:
                this.hikAlarmDataService.setupAlarmChan(obj.getJSONObject("data").getString("deviceSn"));
                break;
            case 1003:
                this.hikAlarmDataService.closeAlarmChan(obj.getJSONObject("data").getString("deviceSn"));
                break;
            default:
                log.info("default:{}", obj);
        }
    }
}
