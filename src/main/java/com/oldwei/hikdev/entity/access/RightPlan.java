package com.oldwei.hikdev.entity.access;

import lombok.Data;

import java.io.Serializable;

/**
 * @author oldwei
 * @date 2021-7-8 11:19
 */
@Data
public class RightPlan implements Serializable {
    private static final long serialVersionUID = -527127744025133125L;
    private String planTemplateNo = "1";
    private Integer doorNo = 1;
}
