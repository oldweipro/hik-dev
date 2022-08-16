package com.oldwei.hikdev.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.oldwei.hikdev.entity.device.DeviceLogin;
import com.oldwei.hikdev.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author oldwei
 * @date 2021-5-12 18:04
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccessControlServiceImpl implements IAccessControlService {

    private final IHikCardService hikCardService;

//    private final IHikAlarmDataService hikAlarmDataService;

    private final IHikDeviceService hikDeviceService;

    private final IHikUserService hikUserService;

    @Override
    public String commandMqtt(String command) {
        boolean isJson = false;
        JSONObject result = new JSONObject();
        //错误code=-1 成功code=0没有data code=1有data
        result.put("code", 0);
        try {
            JSON.parse(command);
            isJson = true;
        } catch (Exception e) {
        }
        if (!isJson) {
            //如果不是json格式
            result.put("msg", "传入的字符串，不是json格式");
            return result.toJSONString();
        }
        JSONObject commandObj = JSONObject.parseObject(command);
        String code = commandObj.getString("code");
        if (StrUtil.isBlank(code)) {
            result.put("msg", "缺少必要字段：code");
            return result.toJSONString();
        }
        JSONObject data = commandObj.getJSONObject("data");
        if (null == data) {
            result.put("msg", "缺少必要字段：data");
            return result.toJSONString();
        }
        switch (code) {
            case "HIK_DEVICE_LOGIN":
                //设备登录 Done.
                DeviceLogin deviceLogin = data.toJavaObject(DeviceLogin.class);
                boolean b = this.hikDeviceService.login(deviceLogin);
                if (b) {
                    result.put("code", 0);
                    result.put("event", data.getString("event"));
                    result.put("msg", "设备登录成功");
                } else {
                    result.put("code", -1);
                    result.put("event", data.getString("event"));
                    result.put("msg", "设备登录失败");
                }
                break;
            case "HIK_DEVICE_LOGOUT":
                boolean bool = this.hikDeviceService.clean(data.getString("ip"));
                if (bool) {
                    result.put("code", 0);
                    result.put("event", data.getString("event"));
                    result.put("msg", "设备注销成功");
                } else {
                    result.put("code", -1);
                    result.put("event", data.getString("event"));
                    result.put("msg", "设备注销失败");
                }
                break;
            case "CLOUD_ALL_CARD_BY_DEVICE_IP":
                //查询所有卡 Done.
//                result = this.hikCardService.selectCardInfoByDeviceIp(data);
                break;
            case "CLOUD_SELECT_FACE_BY_CARD_NO":
                //根据卡号查询人脸 Done.
//                result = this.hikCardService.selectFaceByCardNo(data);
                break;
            case "CLOUD_MULTI_DISTRIBUTE_CARD":
                //批量下发卡 Done.
                boolean userCardInfoList = data.containsKey("userCardInfoList");
                if (userCardInfoList) {
                    result = this.hikCardService.distributeMultiCard(data);
                } else {
                    result.put("code", -1);
                    result.put("event", data.getString("event"));
                    result.put("msg", "缺少相关字段: strCardNo或intEmployeeNo或strCardName");
                }
                break;
            case "CLOUD_MULTI_DISTRIBUTE_FACE":
                //批量下发人脸 Done.
                boolean userFaceInfoList = data.containsKey("userFaceInfoList");
                if (userFaceInfoList) {
                    result = this.hikCardService.distributeMultiFace(data);
                } else {
                    result.put("code", -1);
                    result.put("event", data.getString("event"));
                    result.put("msg", "缺少相关字段: strCardNo或intEmployeeNo或strCardName");
                }

                break;
            case "CLOUD_DELETE_CARD_BY_CARD_NO":
                //根据卡号删除卡 注意：先删除人脸再删除卡 Done.
                if (data.containsKey("cardNo")) {
                    result = this.hikCardService.deleteCardByCardNo(data);
                } else {
                    result.put("code", -1);
                    result.put("event", data.getString("event"));
                    result.put("msg", "缺少相关字段: cardNo");
                }
                break;
            case "CLOUD_DELETE_FACE_BY_CARD_NO":
                //根据卡号删除人脸 Done.
                if (data.containsKey("cardNo")) {
                    result = this.hikCardService.deleteFaceByCardNo(data);
                } else {
                    result.put("code", -1);
                    result.put("event", data.getString("event"));
                    result.put("msg", "缺少相关字段: cardNo");
                }
                break;
            case "CLOUD_ORGANIZE_DEFENCE":
                //布防
                if (data.containsKey("ip")) {
//                    result = this.hikAlarmDataService.setupAlarmChan(data.getString("ip"));
                } else {
                    result.put("code", -1);
                    result.put("event", data.getString("event"));
                    result.put("msg", "缺少相关字段: ip");
                }
                break;
            case "CLOUD_WITHDRAW_DEFENCE":
                //撤防
                if (data.containsKey("ip")) {
//                    result = this.hikAlarmDataService.closeAlarmChan(data.getString("ip"));
                } else {
                    result.put("code", -1);
                    result.put("event", data.getString("event"));
                    result.put("msg", "缺少相关字段: ip");
                }
                break;
            case "CLOUD_USER_SELECT_ABILITY":
                //获取设备能力集
                result = this.hikUserService.getAbility(data);
                break;
            case "CLOUD_USER_SELECT_USERINFO":
                //查询用户信息
//                if (data.containsKey("pageNum") && data.containsKey("pageSize")) {
//                    result = this.hikUserService.searchUserInfo(data);
//                } else {
//                    result.put("code", -1);
//                    result.put("event", data.getString("event"));
//                    result.put("msg", "缺少相关字段: pageNum或pageSize");
//                }
                break;
            case "CLOUD_USER_ADD_USERINFO":
                //新增下发用户
//                result = this.hikUserService.addUserInfo(data);
                break;
            case "CLOUD_USER_MODIFY_USERINFO":
                //修改下发用户
//                result = this.hikUserService.modifyUserInfo(data);
                break;
            case "CLOUD_USER_ADD_MULTI_USERINFO":
                //批量下发用户
//                result = this.hikUserService.addMultiUserInfo(data);
                break;
            case "CLOUD_USER_SELECT_FACE":
                //根据工号查询人脸
//                result = this.hikUserService.searchFaceInfo(data);
                break;
            case "CLOUD_USER_ADD_MULTI_FACE":
                //批量下发人脸
//                result = this.hikUserService.addMultiFace(data);
                break;
            case "CLOUD_USER_DELETE_FACE":
                //批量删除人脸
//                result = this.hikUserService.delFaceInfo(data);
                break;
            case "CLOUD_USER_DELETE_USERINFO":
                //批量删除用户
//                result = this.hikUserService.delUserInfo(data);
                break;
            default:
                result.put("code", -1);
                result.put("msg", "未知code");
        }
        return result.toJSONString();
    }
}
