package com.oldwei.hikdev.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.oldwei.hikdev.entity.access.AccessPeople;
import com.oldwei.hikdev.entity.QueryRequest;
import com.oldwei.hikdev.service.IHikUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 门禁 以人为中心
 *
 * @author oldwei
 * @date 2021-7-7 16:57
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/access/people")
public class AccessPeopleController {

    private final IHikUserService hikUserService;

    /**
     * 查询门禁设备中用户数据
     * 分页从0开始
     *
     * @param ip           设备ID
     * @param employeeNos  用户ID employeeNo
     * @param queryRequest 分页数据
     * @return
     */
    @GetMapping("searchPeoples/{ip}")
    public JSONObject searchPeoples(@PathVariable String ip, String employeeNos, QueryRequest queryRequest) {
        String[] ids = {};
        if (StrUtil.isNotBlank(employeeNos)) {
            ids = employeeNos.split(",");
        }
        return this.hikUserService.searchUserInfo(ip, ids, queryRequest);
    }

    /**
     * 根据工号查询人脸
     * TODO 根据工号查询人脸
     *
     * @param ip     设备IP
     * @param people employeeNo
     * @return
     */
    @GetMapping("searchPeopleFace/{ip}")
    public JSONObject searchPeopleFace(@PathVariable String ip, AccessPeople people) {
        return this.hikUserService.searchFaceInfo(ip, people);
    }

    /**
     * 新增用户
     *
     * @param ip     设备IP
     * @param people 工号和真实姓名
     * @return
     */
    @PostMapping("addPeople/{ip}")
    public JSONObject addPeople(@PathVariable String ip, @RequestBody AccessPeople people) {
        return this.hikUserService.addUserInfo(ip, people);
    }

    /**
     * 修改用户
     *
     * @param ip     设备IP
     * @param people 工号和真实姓名
     * @return
     */
    @PostMapping("modifyPeople/{ip}")
    public JSONObject modifyPeople(@PathVariable String ip, @RequestBody AccessPeople people) {
        return this.hikUserService.modifyUserInfo(ip, people);
    }

    /**
     * 批量下发用户
     * TODO 批量下发用户
     *
     * @param ip         设备IP
     * @param peopleList 用户列表
     * @return
     */
    @PostMapping("addMultiPeople/{ip}")
    public JSONObject addMultiPeople(@PathVariable String ip, @RequestBody List<AccessPeople> peopleList) {
        return this.hikUserService.addMultiUserInfo(ip, peopleList);
    }

    /**
     * 批量下发用户人脸
     * TODO 批量下发用户人脸
     *
     * @param ip         设备IP
     * @param peopleList 用户列表
     * @return
     */
    @PostMapping("addMultiPeopleFace/{ip}")
    public JSONObject addMultiPeopleFace(@PathVariable String ip, @RequestBody List<AccessPeople> peopleList) {
        return this.hikUserService.addMultiFace(ip, peopleList);
    }

    /**
     * 根据工号批量删除人脸
     * TODO 根据工号批量删除人脸
     *
     * @param ip          设备IP
     * @param employeeIds 用户ID列表 employeeIds
     * @return
     */
    @PostMapping("delMultiPeopleFace/{ip}")
    public JSONObject delMultiPeopleFace(@PathVariable String ip, @RequestParam String employeeIds) {
        String[] ids = employeeIds.split(",");
        return this.hikUserService.delFaceInfo(ip, ids);
    }

    /**
     * 根据工号批量删除用户
     * TODO 根据工号批量删除用户
     *
     * @param ip          设备IP
     * @param employeeIds 用户ID列表
     * @return
     */
    @PostMapping("delMultiPeople/{ip}")
    public JSONObject delMultiPeople(@PathVariable String ip, @RequestParam String employeeIds) {
        String[] ids = employeeIds.split(",");
        return this.hikUserService.delUserInfo(ip, ids);
    }
}
