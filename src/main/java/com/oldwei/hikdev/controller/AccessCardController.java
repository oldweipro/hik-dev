package com.oldwei.hikdev.controller;

import com.alibaba.fastjson.JSONObject;
import com.oldwei.hikdev.service.IHikCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author oldwei
 * @date 2021-7-7 16:55
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/access/card")
public class AccessCardController {
    private final IHikCardService hikCardService;

    /**
     * 设置
     */
    @GetMapping("setCartTemplate")
    public void setCartTemplate(Integer planTemplateNumber, String ip) {
        this.hikCardService.setCartTemplate(planTemplateNumber, ip);
    }

    /**
     * 根据设备IP查询所有卡信息
     *
     * @param ip
     * @return
     */
    @GetMapping("selectCardInfoByDeviceIp")
    public JSONObject selectCardInfoByDeviceIp(String ip) {
        return this.hikCardService.selectCardInfoByDeviceIp(ip);
    }

    /**
     * 批量下发卡信息
     *
     * @param jsonObject 卡信息
     * @return
     */
    @PostMapping("distributeMultiCard")
    public JSONObject distributeMultiCard(@RequestBody JSONObject jsonObject) {
        return this.hikCardService.distributeMultiCard(jsonObject);
    }

    /**
     * 批量下发人脸
     *
     * @param jsonObject 人脸信息
     * @return
     */
    @PostMapping("distributeMultiFace")
    public JSONObject distributeMultiFace(@RequestBody JSONObject jsonObject) {
        return this.hikCardService.distributeMultiFace(jsonObject);
    }

    /**
     * 根据卡号删除人脸
     *
     * @param jsonObject
     * @return
     */
    @PostMapping("deleteFaceByCardNo")
    public JSONObject deleteFaceByCardNo(@RequestBody JSONObject jsonObject) {
        return this.hikCardService.deleteFaceByCardNo(jsonObject);
    }

    /**
     * 根据卡号删除卡信息
     *
     * @param jsonObject
     * @return
     */
    @PostMapping("deleteCardByCardNo")
    public JSONObject deleteCardByCardNo(@RequestBody JSONObject jsonObject) {
        return this.hikCardService.deleteCardByCardNo(jsonObject);
    }
}
