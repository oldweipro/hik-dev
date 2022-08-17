package com.oldwei.hikdev.controller;

import cn.hutool.core.util.StrUtil;
import com.oldwei.hikdev.annotation.CheckDeviceLogin;
import com.oldwei.hikdev.entity.HikDevResponse;
import com.oldwei.hikdev.entity.QueryRequest;
import com.oldwei.hikdev.entity.access.AccessPeople;
import com.oldwei.hikdev.entity.param.AccessControlUser;
import com.oldwei.hikdev.service.IHikAccessControlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author oldwei
 * @date 2021-9-27 11:34
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/accessControl")
public class AccessControlController {
    private final IHikAccessControlService hikAccessControlService;


    /**
     * 【卡】根据设备序列号deviceSn查询所有卡信息
     *
     * @param deviceSn 设备序列号
     * @return
     */
    @CheckDeviceLogin
    @GetMapping("getAllCardInfo")
    public HikDevResponse getAllCardInfo(String deviceSn) {
        return this.hikAccessControlService.getAllCardInfo(deviceSn);
    }

    /**
     * 【用户】根据设备序列号deviceSn查询所有用户信息
     *
     * @param ip     设备IP
     * @param employeeNos  用户id
     * @param queryRequest 分页参数: pageNum, pageSize
     * @return
     */
    @CheckDeviceLogin
    @GetMapping("getAllUserInfo")
    public HikDevResponse getAllUserInfo(String ip, String employeeNos, QueryRequest queryRequest) {
        String[] ids = {};
        if (StrUtil.isNotBlank(employeeNos)) {
            ids = employeeNos.split(",");
        }
        return this.hikAccessControlService.getAllUserInfo(ip, ids, queryRequest);
    }

    /**
     * 【用户】下发
     *
     * @param accessControlUser 门禁用户: deviceSn, realName, int:employeeNo
     * @return
     */
    @CheckDeviceLogin
    @PostMapping("addUser")
    public HikDevResponse addUser(@RequestBody AccessControlUser accessControlUser) {
        return this.hikAccessControlService.addUser(accessControlUser.getDeviceSn(), accessControlUser);
    }

    /**
     * 【用户】修改
     *
     * @param accessPeople 门禁用户: deviceSn, realName, int:employeeNo
     * @return
     */
    @CheckDeviceLogin
    @PutMapping("modifyUser")
    public HikDevResponse modifyUser(@RequestBody AccessPeople accessPeople) {
        return this.hikAccessControlService.modifyUser(accessPeople);
    }

    /**
     * 【用户】批量下发
     *
     * @param accessControlUserList 门禁用户列表: deviceSn, realName, int:employeeNo
     * @return
     */
    @CheckDeviceLogin
    @PostMapping("addMultiUser/{deviceSn}")
    public HikDevResponse addMultiUser(@PathVariable String deviceSn, @RequestBody List<AccessControlUser> accessControlUserList) {
        return this.hikAccessControlService.addMultiUser(deviceSn, accessControlUserList);
    }

    /**
     * 【用户】下发人脸
     *
     * @param accessControlUser 门禁用户: deviceSn, employeeNo, base64Pic
     * @return
     */
    @CheckDeviceLogin
    @PutMapping("addUserFace")
    public HikDevResponse addUserFace(@RequestBody AccessControlUser accessControlUser) {
        return this.hikAccessControlService.addUserFace(accessControlUser.getDeviceSn(), accessControlUser);
    }

    /**
     * 【用户】批量下发人脸
     *
     * @param accessControlUserList 门禁用户列表: deviceSn, employeeNo, base64Pic
     * @return
     */
    @CheckDeviceLogin
    @PutMapping("addMultiUserFace/{deviceSn}")
    public HikDevResponse addMultiUserFace(@PathVariable String deviceSn, @RequestBody List<AccessControlUser> accessControlUserList) {
        return this.hikAccessControlService.addMultiUserFace(deviceSn, accessControlUserList);
    }

    /**
     * 【用户】批量删除人脸
     *
     * @param deviceSn    设备序列号
     * @param employeeIds 多个用户工号，用逗号隔开
     * @return
     */
    @CheckDeviceLogin
    @DeleteMapping("delMultiUserFace")
    public HikDevResponse delMultiUserFace(String deviceSn, String employeeIds) {
        String[] ids = employeeIds.split(",");
        return this.hikAccessControlService.delMultiUserFace(deviceSn, ids);
    }

    /**
     * 【用户】批量删除用户
     *
     * @param deviceSn    设备序列号
     * @param employeeIds 多个用户工号，用逗号隔开
     * @return
     */
    @CheckDeviceLogin
    @DeleteMapping("delMultiUser")
    public HikDevResponse delMultiUser(String deviceSn, String employeeIds) {
        String[] ids = employeeIds.split(",");
        return this.hikAccessControlService.delMultiUser(deviceSn, ids);
    }

    /**
     * 【卡】批量下发
     *
     * @param accessControlUserList 门禁用户列表: deviceSn, realName, int:employeeNo
     * @return
     */
    @CheckDeviceLogin
    @PostMapping("addMultiCard/{deviceSn}")
    public HikDevResponse addMultiCard(@PathVariable String deviceSn, @RequestBody List<AccessControlUser> accessControlUserList) {
        return this.hikAccessControlService.addMultiCard(deviceSn, accessControlUserList);
    }

    /**
     * 【卡】批量下发人脸
     *
     * @param accessControlUserList 门禁用户列表: deviceSn, realName, int:employeeNo
     * @return
     */
    @CheckDeviceLogin
    @PutMapping("addMultiCardFace/{deviceSn}")
    public HikDevResponse addMultiCardFace(@PathVariable String deviceSn, @RequestBody List<AccessControlUser> accessControlUserList) {
        return this.hikAccessControlService.addMultiCardFace(deviceSn, accessControlUserList);
    }

    /**
     * 【卡】批量删除人脸
     *
     * @param deviceSn  设备被序列号
     * @param cardNoIds 将要被删除人脸的卡号数组
     * @return
     */
    @CheckDeviceLogin
    @DeleteMapping("delMultiCardFace")
    public HikDevResponse delMultiCardFace(String deviceSn, String cardNoIds) {
        String[] ids = cardNoIds.split(",");
        return this.hikAccessControlService.delMultiCardFace(deviceSn, ids);
    }

    /**
     * 【卡】批量删除卡
     *
     * @param deviceSn  设备被序列号
     * @param cardNoIds 将要被删除人脸的卡号数组
     * @return
     */
    @CheckDeviceLogin
    @DeleteMapping("delMultiCard")
    public HikDevResponse delMultiCard(String deviceSn, String cardNoIds) {
        String[] ids = cardNoIds.split(",");
        return this.hikAccessControlService.delMultiCard(deviceSn, ids);
    }

    /**
     * 【卡】设置计划模板
     *
     * @param deviceSn  设备被序列号
     * @param planTemplateNumber 将要被删除人脸的卡号数组
     * @return
     */
    @CheckDeviceLogin
    @PostMapping("setCartTemplate")
    public HikDevResponse setCartTemplate(String deviceSn, Integer planTemplateNumber) {
        return this.hikAccessControlService.setCartTemplate(deviceSn, planTemplateNumber);
    }
}
