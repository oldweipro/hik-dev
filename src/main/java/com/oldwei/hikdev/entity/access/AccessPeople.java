package com.oldwei.hikdev.entity.access;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author oldwei
 * @date 2021-7-7 17:00
 */
@Data
public class AccessPeople implements Serializable {
    private static final long serialVersionUID = 6585020083695187516L;
    private String ipv4Address;
    private Integer employeeNo;
    @JSONField(name = "name", serialize = false)
    private String realName;
    @JSONField(deserialize = false)
    private String name;
    private String base64Pic;
    private String doorRight = "1";
    private Integer roomNumber = 1;
    private String gender;
    private Integer numOfCard;
    private Boolean closeDelayEnabled;
    private String password = "123456";
    private String belongGroup;
    private Integer maxOpenDoorTime = 0;
    private Integer openDoorTime;
    private Integer floorNumber = 1;
    @JSONField(name = "localUIRight")
    private Boolean localUiRight;
    private String userType = "normal";
    private Integer numOfFace;
    private Boolean openDelayEnabled = false;
    private Boolean checkUser = false;
    private List<RightPlan> rightPlan;
    @JSONField(name = "PersonInfoExtends")
    private List<PersonInfoExtends> personInfoExtends;
    @JSONField(name = "Valid")
    private Valid valid;
}
