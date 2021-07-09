package com.oldwei.hikdev.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 流地址
 *
 * @author oldwei
 * @date 2021-7-9 16:28
 */
@Data
public class StreamAddress implements Serializable {
    private static final long serialVersionUID = 579722006545556209L;
    private String rtmp;
    private String hls;
    private String flv;
}
