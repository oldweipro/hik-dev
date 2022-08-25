package com.oldwei.hikdev.structure;

import com.oldwei.hikdev.constant.HikConstant;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2022/8/25 10:03
 */
public class NET_DVR_PU_STREAM_CFG_V41 extends Structure {
    public int dwSize;
    public byte byStreamMode;/*取流模式，0-无效，1-通过IP或域名取流，2-通过URL取流,3-通过动态域名解析向设备取流*/
    public byte byStreamEncrypt;  //是否进行码流加密处理,0-不支持,1-支持
    public byte[] byRes1 = new byte[2];
    public NET_DVR_DEC_STREAM_MODE uDecStreamMode;//取流信息
    public int dwDecDelayTime;//解码延时时间，单位：毫秒
    public byte[] sStreamPassword = new byte[HikConstant.STREAM_PASSWD_LEN];  //码流加密密码,需敏感信息加密
    public byte[] byRes2 = new byte[48];
}