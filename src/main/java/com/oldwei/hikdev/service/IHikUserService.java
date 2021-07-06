package com.oldwei.hikdev.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.UnsupportedEncodingException;


/**
 * @author zhangjie
 * @date 2021-5-14 17:16
 */
public interface IHikUserService {

    /**
     * 获取设备能力集
     * @param jsonObject jsonObject
     * @return map
     */
    JSONObject getAbility(JSONObject jsonObject);

    /**
     * 查询用户信息
     * @param jsonObject jsonObject
     * @return JSONObject
     */
    JSONObject searchUserInfo(JSONObject jsonObject);

    /**
     * 新增下发用户
     * @param jsonObject jsonObject
     * @return JSONObject
     * @throws UnsupportedEncodingException  UnsupportedEncodingException
     * @throws InterruptedException  InterruptedException
     */
    JSONObject addUserInfo(JSONObject jsonObject);

    /**
     * 修改用户
     * @param jsonObject jsonObject
     * @return JSONObject
     * @throws UnsupportedEncodingException  UnsupportedEncodingException
     * @throws InterruptedException  InterruptedException
     */
    JSONObject modifyUserInfo(JSONObject jsonObject);

    /**
     * 批量新增下发用户
     * @param jsonObject jsonObject
     * @return JSONObject
     * @throws UnsupportedEncodingException  UnsupportedEncodingException
     * @throws InterruptedException  InterruptedException
     */
    JSONObject addMultiUserInfo(JSONObject jsonObject);

    /**
     * 查询人脸信息
     * @param jsonObject jsonObject
     * @return JSONObject
     */
    JSONObject searchFaceInfo(JSONObject jsonObject);

    /**
     * 批量添加人脸
     * @param jsonObject jsonObject
     * @return JSONObject
     */
    JSONObject addMultiFace(JSONObject jsonObject);

    /**
     * 删除脸信息
     * @param jsonObject jsonObject
     * @return JSONObject
     */
    JSONObject delFaceInfo(JSONObject jsonObject);

    /**
     * 删除人信息
     * @param jsonObject jsonObject
     * @return JSONObject
     */
    JSONObject delUserInfo(JSONObject jsonObject);
}
