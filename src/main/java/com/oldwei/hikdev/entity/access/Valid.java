package com.oldwei.hikdev.entity.access;

import lombok.Data;

import java.io.Serializable;

/**
 * @author oldwei
 * @date 2021-7-8 11:12
 */
@Data
public class Valid implements Serializable {

    private static final long serialVersionUID = 5595086123551302326L;
    private Boolean enable;
    private String timeType;
    private String beginTime;
    private String endTime;

}
