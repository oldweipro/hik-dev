package com.oldwei.hikdev.controller;

import cn.hutool.core.util.StrUtil;
import com.oldwei.hikdev.annotation.CheckDeviceLogin;
import com.oldwei.hikdev.entity.HikDevResponse;
import com.oldwei.hikdev.entity.QueryRequest;
import com.oldwei.hikdev.entity.access.AccessPeople;
import com.oldwei.hikdev.entity.param.AccessControlUser;
import com.oldwei.hikdev.entity.param.DeviceIp;
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
     * @param ipv4Address 设备IP
     * @return HikDevResponse
     */
    @CheckDeviceLogin
    @GetMapping("getAllCardInfo")
    public HikDevResponse getAllCardInfo(String ipv4Address) {
        return this.hikAccessControlService.getAllCardInfo(ipv4Address);
    }

    /**
     * 【用户】根据设备ip查询所有用户信息
     *
     * @param ipv4Address           设备IP
     * @param employeeNos  用户id
     * @param queryRequest 分页参数: pageNum, pageSize
     * @return HikDevResponse
     */
    @CheckDeviceLogin
    @GetMapping("getAllUserInfo")
    public HikDevResponse getAllUserInfo(String ipv4Address, String employeeNos, QueryRequest queryRequest) {
        String[] ids = {};
        if (StrUtil.isNotBlank(employeeNos)) {
            ids = employeeNos.split(",");
        }
        return this.hikAccessControlService.getAllUserInfo(ipv4Address, ids, queryRequest);
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
        return this.hikAccessControlService.addUser(accessControlUser.getIpv4Address(), accessControlUser);
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
    @PostMapping("addMultiUser/{ipv4Address}")
    public HikDevResponse addMultiUser(@PathVariable String ipv4Address, @RequestBody List<AccessControlUser> accessControlUserList) {
        return this.hikAccessControlService.addMultiUser(ipv4Address, accessControlUserList);
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
        return this.hikAccessControlService.addUserFace(accessControlUser.getIpv4Address(), accessControlUser);
    }

    /**
     * 【用户】批量下发人脸
     *
     * @param accessControlUserList 门禁用户列表: ip, employeeNo, base64Pic
     * @return HikDevResponse
     */
    @CheckDeviceLogin
    @PutMapping("addMultiUserFace/{ipv4Address}")
    public HikDevResponse addMultiUserFace(@PathVariable String ipv4Address, @RequestBody List<AccessControlUser> accessControlUserList) {
        return this.hikAccessControlService.addMultiUserFace(ipv4Address, accessControlUserList);
    }

    /**
     * 【用户】批量删除人脸
     *
     * @param ipv4Address          设备ip
     * @param employeeIds 多个用户工号，用逗号隔开
     * @return HikDevResponse
     */
    @CheckDeviceLogin
    @DeleteMapping("delMultiUserFace")
    public HikDevResponse delMultiUserFace(String ipv4Address, String employeeIds) {
        String[] ids = employeeIds.split(",");
        return this.hikAccessControlService.delMultiUserFace(ipv4Address, ids);
    }

    /**
     * 【用户】批量删除用户
     *
     * @param ipv4Address          设备ip
     * @param employeeIds 多个用户工号，用逗号隔开
     * @return HikDevResponse
     */
    @CheckDeviceLogin
    @DeleteMapping("delMultiUser")
    public HikDevResponse delMultiUser(String ipv4Address, String employeeIds) {
        String[] ids = employeeIds.split(",");
        return this.hikAccessControlService.delMultiUser(ipv4Address, ids);
    }

    /**
     * 【卡】批量下发
     *
     * @param accessControlUserList 门禁用户列表: ip, realName, int:employeeNo
     * @return HikDevResponse
     */
    @CheckDeviceLogin
    @PostMapping("addMultiCard/{ipv4Address}")
    public HikDevResponse addMultiCard(@PathVariable String ipv4Address, @RequestBody List<AccessControlUser> accessControlUserList) {
        return this.hikAccessControlService.addMultiCard(ipv4Address, accessControlUserList);
    }

    /**
     * 【卡】批量下发人脸
     *
     * @param accessControlUserList 门禁用户列表: ip, realName, int:employeeNo
     * @return HikDevResponse
     */
    @CheckDeviceLogin
    @PutMapping("addMultiCardFace/{ipv4Address}")
    public HikDevResponse addMultiCardFace(@PathVariable String ipv4Address, @RequestBody List<AccessControlUser> accessControlUserList) {
        return this.hikAccessControlService.addMultiCardFace(ipv4Address, accessControlUserList);
    }

    /**
     * 【卡】批量删除人脸
     *
     * @param ipv4Address        设备ip
     * @param cardNoIds 将要被删除人脸的卡号数组
     * @return HikDevResponse
     */
    @CheckDeviceLogin
    @DeleteMapping("delMultiCardFace")
    public HikDevResponse delMultiCardFace(String ipv4Address, String cardNoIds) {
        String[] ids = cardNoIds.split(",");
        return this.hikAccessControlService.delMultiCardFace(ipv4Address, ids);
    }

    /**
     * 【卡】批量删除卡
     *
     * @param ipv4Address        设备ip
     * @param cardNoIds 将要被删除人脸的卡号数组
     * @return HikDevResponse
     */
    @CheckDeviceLogin
    @DeleteMapping("delMultiCard")
    public HikDevResponse delMultiCard(String ipv4Address, String cardNoIds) {
        String[] ids = cardNoIds.split(",");
        return this.hikAccessControlService.delMultiCard(ipv4Address, ids);
    }

    /**
     * 【卡】设置计划模板
     *
     * @param ipv4Address                 设备ip
     * @param planTemplateNumber 将要被删除人脸的卡号数组
     * @return HikDevResponse
     */
    @CheckDeviceLogin
    @PostMapping("setCartTemplate")
    public HikDevResponse setCartTemplate(String ipv4Address, Integer planTemplateNumber) {
        return this.hikAccessControlService.setCartTemplate(ipv4Address, planTemplateNumber);
    }

    /**
     * 远程门禁控制或梯控控制。
     * lUserID
     * [in] NET_DVR_Login_V40等登录接口的返回值
     * lGatewayIndex
     * [in] 门禁序号（楼层编号、锁ID），从1开始，-1表示对所有门（或者梯控的所有楼层）进行操作
     * dwStaic
     * [in] 命令值：0- 关闭（对于梯控，表示受控），1- 打开（对于梯控，表示开门），2- 常开（对于梯控，表示自由、通道状态），3- 常关（对于梯控，表示禁用），4- 恢复（梯控，普通状态），5- 访客呼梯（梯控），6- 住户呼梯（梯控）
     *
     * @param ipv4Address 设备ip
     * @return HikDevResponse
     */
    @CheckDeviceLogin
    @GetMapping("openTheDoor")
    public HikDevResponse openTheDoor(String ipv4Address) {
        Integer loginId = ConfigJsonUtil.getDeviceSearchInfoByIp(ipv4Address).getLoginId();
        boolean b = this.hikDevService.NET_DVR_ControlGateway(loginId, -1, 1);
        if (b) {
            return new HikDevResponse().ok();
        }
        return new HikDevResponse().err("开门失败");
    }
}
