package com.oldwei.hikdev.controller;

import cn.hutool.core.util.StrUtil;
import com.oldwei.hikdev.annotation.CheckDeviceLogin;
import com.oldwei.hikdev.entity.HikDevResponse;
import com.oldwei.hikdev.entity.QueryRequest;
import com.oldwei.hikdev.entity.access.AccessPeople;
import com.oldwei.hikdev.entity.param.AccessControlUser;
import com.oldwei.hikdev.service.IHikAccessControlService;
import com.oldwei.hikdev.service.IHikDevService;
import com.oldwei.hikdev.util.ConfigJsonUtil;
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

    private final IHikDevService hikDevService;


    /**
     * 【卡】根据设备序列号deviceSn查询所有卡信息
     *
     * @param deviceSn 设备序列号
     * @return HikDevResponse
     */
    @CheckDeviceLogin
    @GetMapping("getAllCardInfo")
    public HikDevResponse getAllCardInfo(String deviceSn) {
        return this.hikAccessControlService.getAllCardInfo(deviceSn);
    }

    /**
     * 【用户】根据设备ip查询所有用户信息
     *
     * @param ip           设备IP
     * @param employeeNos  用户id
     * @param queryRequest 分页参数: pageNum, pageSize
     * @return HikDevResponse
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
     * @param accessControlUser 门禁用户: ip, realName, int:employeeNo
     * @return HikDevResponse
     */
    @CheckDeviceLogin
    @PostMapping("addUser")
    public HikDevResponse addUser(@RequestBody AccessControlUser accessControlUser) {
        return this.hikAccessControlService.addUser(accessControlUser.getIp(), accessControlUser);
    }

    /**
     * 【用户】修改
     *
     * @param accessPeople 门禁用户: ip, realName, int:employeeNo
     * @return HikDevResponse
     */
    @CheckDeviceLogin
    @PutMapping("modifyUser")
    public HikDevResponse modifyUser(@RequestBody AccessPeople accessPeople) {
        return this.hikAccessControlService.modifyUser(accessPeople);
    }

    /**
     * 【用户】批量下发
     *
     * @param accessControlUserList 门禁用户列表: ip, realName, int:employeeNo
     * @return HikDevResponse
     */
    @CheckDeviceLogin
    @PostMapping("addMultiUser/{ip}")
    public HikDevResponse addMultiUser(@PathVariable String ip, @RequestBody List<AccessControlUser> accessControlUserList) {
        return this.hikAccessControlService.addMultiUser(ip, accessControlUserList);
    }

    /**
     * 【用户】下发人脸
     *
     * @param accessControlUser 门禁用户: ip, employeeNo, base64Pic
     * @return HikDevResponse
     */
    @CheckDeviceLogin
    @PutMapping("addUserFace")
    public HikDevResponse addUserFace(@RequestBody AccessControlUser accessControlUser) {
        return this.hikAccessControlService.addUserFace(accessControlUser.getIp(), accessControlUser);
    }

    /**
     * 【用户】批量下发人脸
     *
     * @param accessControlUserList 门禁用户列表: ip, employeeNo, base64Pic
     * @return HikDevResponse
     */
    @CheckDeviceLogin
    @PutMapping("addMultiUserFace/{ip}")
    public HikDevResponse addMultiUserFace(@PathVariable String ip, @RequestBody List<AccessControlUser> accessControlUserList) {
        return this.hikAccessControlService.addMultiUserFace(ip, accessControlUserList);
    }

    /**
     * 【用户】批量删除人脸
     *
     * @param ip          设备ip
     * @param employeeIds 多个用户工号，用逗号隔开
     * @return HikDevResponse
     */
    @CheckDeviceLogin
    @DeleteMapping("delMultiUserFace")
    public HikDevResponse delMultiUserFace(String ip, String employeeIds) {
        String[] ids = employeeIds.split(",");
        return this.hikAccessControlService.delMultiUserFace(ip, ids);
    }

    /**
     * 【用户】批量删除用户
     *
     * @param ip          设备ip
     * @param employeeIds 多个用户工号，用逗号隔开
     * @return HikDevResponse
     */
    @CheckDeviceLogin
    @DeleteMapping("delMultiUser")
    public HikDevResponse delMultiUser(String ip, String employeeIds) {
        String[] ids = employeeIds.split(",");
        return this.hikAccessControlService.delMultiUser(ip, ids);
    }

    /**
     * 【卡】批量下发
     *
     * @param accessControlUserList 门禁用户列表: ip, realName, int:employeeNo
     * @return HikDevResponse
     */
    @CheckDeviceLogin
    @PostMapping("addMultiCard/{ip}")
    public HikDevResponse addMultiCard(@PathVariable String ip, @RequestBody List<AccessControlUser> accessControlUserList) {
        return this.hikAccessControlService.addMultiCard(ip, accessControlUserList);
    }

    /**
     * 【卡】批量下发人脸
     *
     * @param accessControlUserList 门禁用户列表: ip, realName, int:employeeNo
     * @return HikDevResponse
     */
    @CheckDeviceLogin
    @PutMapping("addMultiCardFace/{ip}")
    public HikDevResponse addMultiCardFace(@PathVariable String ip, @RequestBody List<AccessControlUser> accessControlUserList) {
        return this.hikAccessControlService.addMultiCardFace(ip, accessControlUserList);
    }

    /**
     * 【卡】批量删除人脸
     *
     * @param ip        设备ip
     * @param cardNoIds 将要被删除人脸的卡号数组
     * @return HikDevResponse
     */
    @CheckDeviceLogin
    @DeleteMapping("delMultiCardFace")
    public HikDevResponse delMultiCardFace(String ip, String cardNoIds) {
        String[] ids = cardNoIds.split(",");
        return this.hikAccessControlService.delMultiCardFace(ip, ids);
    }

    /**
     * 【卡】批量删除卡
     *
     * @param ip        设备ip
     * @param cardNoIds 将要被删除人脸的卡号数组
     * @return HikDevResponse
     */
    @CheckDeviceLogin
    @DeleteMapping("delMultiCard")
    public HikDevResponse delMultiCard(String ip, String cardNoIds) {
        String[] ids = cardNoIds.split(",");
        return this.hikAccessControlService.delMultiCard(ip, ids);
    }

    /**
     * 【卡】设置计划模板
     *
     * @param ip                 设备ip
     * @param planTemplateNumber 将要被删除人脸的卡号数组
     * @return HikDevResponse
     */
    @CheckDeviceLogin
    @PostMapping("setCartTemplate")
    public HikDevResponse setCartTemplate(String ip, Integer planTemplateNumber) {
        return this.hikAccessControlService.setCartTemplate(ip, planTemplateNumber);
    }

    /**
     * 开门
     *
     * @param ip 设备ip
     * @return HikDevResponse
     */
    @GetMapping("openTheDoor")
    public HikDevResponse openTheDoor(String ip) {
        Integer loginId = ConfigJsonUtil.getDeviceSearchInfoByIp(ip).getLoginId();
        boolean b = this.hikDevService.NET_DVR_ControlGateway(loginId, -1, 1);
        if (b) {
            return new HikDevResponse().ok();
        }
        return new HikDevResponse().err("开门失败");
    }
}
