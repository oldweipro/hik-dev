package com.oldwei.hikdev.entity.access;

import com.oldwei.hikdev.entity.access.PersonInfoExtends;
import com.oldwei.hikdev.entity.access.RightPlan;
import com.oldwei.hikdev.entity.access.Valid;
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
    private String employeeNo;
    private String realName;
    private byte[] name;
    private String base64Pic;
    private String doorRight;
    private Integer roomNumber;
    private String gender;
    private Integer numOfCard;
    private Boolean closeDelayEnabled;
    private String password;
    private String belongGroup;
    private Integer maxOpenDoorTime;
    private Integer openDoorTime;
    private Integer floorNumber;
    private Boolean localUIRight;
    private String userType;
    private Integer numOfFace;
    private List<RightPlan> rightPlan;
    private List<PersonInfoExtends> personInfoExtends;
    private Valid valid;
}
