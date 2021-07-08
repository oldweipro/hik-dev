package com.oldwei.hikdev.entity.access;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author oldwei
 * @date 2021-7-8 11:24
 */
@Data
public class UserInfoSearch implements Serializable {
    private static final long serialVersionUID = -2921813780729268051L;
    private String responseStatusStrg;
    private String searchID;
    private Integer numOfMatches;
    private Integer totalMatches;
    private List<AccessPeople> userInfo;
}
