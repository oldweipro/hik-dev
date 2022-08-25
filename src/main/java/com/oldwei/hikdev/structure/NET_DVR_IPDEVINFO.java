package com.oldwei.hikdev.structure;

import com.oldwei.hikdev.constant.HikConstant;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2022/8/25 9:18
 */
public class NET_DVR_IPDEVINFO extends Structure {/* IP设备结构 */
    public   int dwEnable;				    /* 该IP设备是否启用 */
    public   byte[] sUserName = new byte[HikConstant.NAME_LEN];		/* 用户名 */
    public   byte[] sPassword = new byte[HikConstant.PASSWD_LEN];	    /* 密码 */
    public NET_DVR_IPADDR struIP = new NET_DVR_IPADDR();			/* IP地址 */
    public   short wDVRPort;			 	    /* 端口号 */
    public   byte[] byres = new byte[34];				/* 保留 */


}