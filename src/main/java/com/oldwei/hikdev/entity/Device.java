package com.oldwei.hikdev.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author oldwei
 * @date 2021-7-7 14:42
 */
@Data
public class Device implements Serializable {
    private static final long serialVersionUID = 7342965103728984523L;
    private String ip;
    private String username;
    private String password;
    private Short port;
    private String title;
    private String flv;
    private String hls;
    private String rtmp;
    private Boolean useAsync = false;
    private Byte type;
    private String rtspUrl;
    private String pushUrl;
}
