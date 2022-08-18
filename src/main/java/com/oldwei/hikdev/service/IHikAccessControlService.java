package com.oldwei.hikdev.service;

import com.oldwei.hikdev.entity.HikDevResponse;
import com.oldwei.hikdev.entity.QueryRequest;
import com.oldwei.hikdev.entity.access.AccessPeople;
import com.oldwei.hikdev.entity.param.AccessControlUser;

import java.util.List;

/**
 * @author oldwei
 * @date 2021-9-27 11:36
 */
public interface IHikAccessControlService {
    /**
     * 【卡】根据设备ip获取所有卡信息
     *
     * @param ip 设备ip
     * @return
     */
    HikDevResponse getAllCardInfo(String ip);

    /**
     * 【用户】根据设备序列号ip获取所有用户信息
     *
     * @param ip           设备IP
     * @param employeeNos  用户id
     * @param queryRequest 分页参数
     * @return
     */
    HikDevResponse getAllUserInfo(String ip, String[] employeeNos, QueryRequest queryRequest);

    /**
     * 【用户】下发
     *
     * @param accessControlUser 门禁用户: ip, realName, int:employeeNo
     * @return
     */
    HikDevResponse addUser(String ip, AccessControlUser accessControlUser);

    /**
     * 【用户】修改
     *
     * @param accessPeople 门禁用户: ip, realName, int:employeeNo
     * @return
     */
    HikDevResponse modifyUser(String ip, AccessPeople accessPeople);

    /**
     * 【用户】批量下发
     *
     * @param accessControlUserList 门禁用户列表: ip, realName, int:employeeNo
     * @return
     */
    HikDevResponse addMultiUser(String ip, List<AccessControlUser> accessControlUserList);

    /**
     * 【用户】下发人脸
     *
     * @param ip                门禁ip
     * @param accessControlUser 门禁用户: employeeNo, base64Pic
     * @return
     */
    HikDevResponse addUserFace(String ip, AccessControlUser accessControlUser);

    /**
     * 【用户】批量下发人脸
     *
     * @param ip                    门禁ip
     * @param accessControlUserList 门禁用户列表: employeeNo, base64Pic
     * @return
     */
    HikDevResponse addMultiUserFace(String ip, List<AccessControlUser> accessControlUserList);

    /**
     * 【用户】批量删除人脸
     *
     * @param ip          设备ip
     * @param employeeIds 多个用户工号，用逗号隔开
     * @return
     */
    HikDevResponse delMultiUserFace(String ip, String[] employeeIds);

    /**
     * 【用户】批量删除用户
     *
     * @param ip          设备ip
     * @param employeeIds 多个用户工号，用逗号隔开
     * @return
     */
    HikDevResponse delMultiUser(String ip, String[] employeeIds);

    /**
     * 【卡】批量下发
     *
     * @param ip                    设备ip
     * @param accessControlUserList 门禁用户列表: employeeNo, realName, cardNo, planTemplateNumber
     * @return
     */
    HikDevResponse addMultiCard(String ip, List<AccessControlUser> accessControlUserList);

    /**
     * 【卡】批量下发人脸
     *
     * @param ip                    设备ip
     * @param accessControlUserList 门禁用户列表: employeeNo, base64Pic
     * @return
     */
    HikDevResponse addMultiCardFace(String ip, List<AccessControlUser> accessControlUserList);

    /**
     * 【卡】批量删除人脸
     *
     * @param ip        设备ip
     * @param cardNoIds 将要被删除人脸的卡号数组
     * @return
     */
    HikDevResponse delMultiCardFace(String ip, String[] cardNoIds);

    /**
     * 【卡】批量删除卡
     *
     * @param ip        设备ip
     * @param cardNoIds 将要被删除人脸的卡号数组
     * @return
     */
    HikDevResponse delMultiCard(String ip, String[] cardNoIds);

    /**
     * 【卡】设置计划模板
     *
     * @param ip                 设备ip
     * @param planTemplateNumber 计划模板编号
     * @return
     */
    HikDevResponse setCartTemplate(String ip, Integer planTemplateNumber);
}
