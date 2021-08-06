package com.oldwei.hikdev.service;


import com.alibaba.fastjson.JSONObject;
import com.oldwei.hikdev.entity.access.AccessPeople;
import com.oldwei.hikdev.entity.QueryRequest;

import java.io.UnsupportedEncodingException;
import java.util.List;


/**
 * @author zhangjie
 * @date 2021-5-14 17:16
 */
public interface IHikUserService {

    /**
     * 获取设备能力集
     *
     * @param jsonObject jsonObject
     * @return map
     */
    JSONObject getAbility(JSONObject jsonObject);

    /**
     * 查询用户信息
     *
     * @param ip       jsonObject
     * @param employeeNos jsonObject
     * @param queryRequest jsonObject
     * @return JSONObject
     */
    JSONObject searchUserInfo(String ip, String[] employeeNos, QueryRequest queryRequest);

    /**
     * 新增下发用户
     *
     * @param people jsonObject
     * @return JSONObject
     */
    JSONObject addUserInfo(String ip, AccessPeople people);

    /**
     * 修改用户
     *
     * @param people jsonObject
     * @return JSONObject
     */
    JSONObject modifyUserInfo(String ip, AccessPeople people);

    /**
     * 批量新增下发用户
     *
     * @param peopleList 用户列表
     * @return JSONObject
     */
    JSONObject addMultiUserInfo(String ip, List<AccessPeople> peopleList);

    /**
     * 查询人脸信息
     *
     * @param ip 设备ID
     * @param people employeeId
     * @return JSONObject
     */
    JSONObject searchFaceInfo(String ip, AccessPeople people);

    /**
     * 查询人脸信息
     *
     * @param ip 设备ID
     * @param people employeeId
     * @return JSONObject
     */
    JSONObject addPeopleFace(String ip, AccessPeople people);


    /**
     * 批量添加人脸
     *
     * @param ip         jsonObject
     * @param peopleList jsonObject
     * @return JSONObject
     */
    JSONObject addMultiFace(String ip, List<AccessPeople> peopleList);

    /**
     * 删除脸信息
     *
     * @param ip          ip
     * @param employeeIds ids
     * @return JSONObject
     */
    JSONObject delFaceInfo(String ip, String[] employeeIds);

    /**
     * 删除人信息
     *
     * @param ip jsonObject
     * @param employeeIds jsonObject
     * @return JSONObject
     */
    JSONObject delUserInfo(String ip, String[] employeeIds);
}
