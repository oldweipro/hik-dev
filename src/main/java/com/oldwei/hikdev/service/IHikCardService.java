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
     * @param jsonObject
     * @return
     */
    JSONObject selectFaceByCardNo(JSONObject jsonObject);

    /**
     * 根据设备IP查询所有卡信息
     *
     * @param jsonObject
     * @return
     */
    JSONObject selectCardInfoByDeviceIp(JSONObject jsonObject);

    /**
     * 批量下发门禁卡
     *
     * @param jsonObject
     * @return
     */
    JSONObject distributeMultiCard(JSONObject jsonObject);

    /**
     * 下发人脸
     * @param jsonObject
     * @return
     */
    JSONObject distributeMultiFace(JSONObject jsonObject);

    /**
     * 根据卡号删除卡
     * @param jsonObject
     * @return
     */
    JSONObject deleteCardByCardNo(JSONObject jsonObject);

    /**
     * 根据卡号删除人脸
     * @param jsonObject
     * @return
     */
    JSONObject deleteFaceByCardNo(JSONObject jsonObject);

}
