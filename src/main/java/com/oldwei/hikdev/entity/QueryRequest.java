package com.oldwei.hikdev.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author oldwei
 * @date 2021-7-7 17:16
 */
@Data
public class QueryRequest implements Serializable {
    private static final long serialVersionUID = 8686167312909730168L;
    private Integer pageNum;
    private Integer pageSize;
}
