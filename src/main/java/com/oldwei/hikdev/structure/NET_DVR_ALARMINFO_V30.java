package com.oldwei.hikdev.structure;

import com.oldwei.hikdev.constant.HikConstant;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 12:19
 */
public class NET_DVR_ALARMINFO_V30 extends Structure {//上传报警信息(9000扩展)
    /**
     * 0-信号量报警,1-硬盘满,2-信号丢失,3－移动侦测,4－硬盘未格式化,5-读写硬盘出错,6-遮挡报警,7-制式不匹配, 8-非法访问, 0xa-GPS定位信息(车载定制)
     */
    public int dwAlarmType;
    /**
     * 报警输入端口
     */
    public int dwAlarmInputNumber;
    /**
     * 触发的输出端口，为1表示对应输出
     */
    public byte[] byAlarmOutputNumber = new byte[HikConstant.MAX_ALARMOUT_V30];
    /**
     * 触发的录像通道，为1表示对应录像, dwAlarmRelateChannel[0]对应第1个通道
     */
    public byte[] byAlarmRelateChannel = new byte[HikConstant.MAX_CHANNUM_V30];
    /**
     * dwAlarmType为2或3,6时，表示哪个通道，dwChannel[0]对应第1个通道
     */
    public byte[] byChannel = new byte[HikConstant.MAX_CHANNUM_V30];
    /**
     * dwAlarmType为1,4,5时,表示哪个硬盘, dwDiskNumber[0]对应第1个硬盘
     */
    public byte[] byDiskNumber = new byte[HikConstant.MAX_DISKNUM_V30];
}
