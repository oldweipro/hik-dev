package com.oldwei.hikdev.service;

import com.alibaba.fastjson.JSONObject;

/**
 * @author oldwei
 * @date 2021-5-14 17:16
 */
public interface IHikCardService {
    /**
     * 根据cardNo查询人脸信息
     *
     * @param strCardNo
     * @param ip
     * @return
     */
    String selectFaceByCardNo(String strCardNo, String ip);

    /**
     * 根据cardNo查询人员信息
     *
     * @param strCardNo
     * @param ip
     * @return
     */
    String selectPersonByCardNo(String strCardNo, String ip);

    /**
     * 根据设备IP查询所有卡信息
     *
     * @param ip
     * @return
     */
    JSONObject selectCardInfoByDeviceIp(String ip);

    /**
     * 批量下发门禁卡
     *
     * @param jsonObject
     * @return
     */
    JSONObject distributeMultiCard(JSONObject jsonObject);

    /**
     * 下发人脸
     *
     * @param jsonObject
     * @return
     */
    JSONObject distributeMultiFace(JSONObject jsonObject);

    /**
     * 根据卡号删除卡
     *
     * @param jsonObject
     * @return
     */
    JSONObject deleteCardByCardNo(JSONObject jsonObject);

    /**
     * 根据卡号删除人脸
     *
     * @param jsonObject
     * @return
     */
    JSONObject deleteFaceByCardNo(JSONObject jsonObject);

    /**
     * 设置计划模板
     *
     * @param iPlanTemplateNumber
     * @param ip
     */
    void setCartTemplate(int iPlanTemplateNumber, String ip);

}
