package com.oldwei.hikdev.entity.access;

import com.alibaba.fastjson.annotation.JSONField;
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

    @JSONField(name = "responseStatusStrg")
    private String responseStatus;
    @JSONField(name = "searchID")
    private String searchId;
    private Integer pageNum;
    @JSONField(name = "numOfMatches")
    private Integer pageSize;
    @JSONField(name = "totalMatches")
    private Integer totals;
    @JSONField(name = "UserInfo")
    private List<AccessPeople> userInfo;
}
