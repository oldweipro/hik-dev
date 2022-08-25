package com.oldwei.hikdev.structure;

import com.oldwei.hikdev.constant.HikConstant;
import com.sun.jna.Structure;

/**
 * @author sunyuchang
 * @date 2022-8-25 12:25
 */
public class NET_DVR_DEVICECFG extends Structure {
    public int dwSize;
    public byte[] sDVRName = new byte[HikConstant.NAME_LEN];     //DVR名称
    public int dwDVRID;				 //DVR ID,用于遥控器 //V1.4(0-99), V1.5(0-255)
    public int dwRecycleRecord;		         //是否循环录像,0:不是; 1:是
    //以下不可更改
    public byte[] sSerialNumber = new byte[HikConstant.SERIALNO_LEN];  //序列号
    public int dwSoftwareVersion;			       //软件版本号,高16位是主版本,低16位是次版本
    public int dwSoftwareBuildDate;			        //软件生成日期,0xYYYYMMDD
    public int dwDSPSoftwareVersion;		        //DSP软件版本,高16位是主版本,低16位是次版本
    public int dwDSPSoftwareBuildDate;		        // DSP软件生成日期,0xYYYYMMDD
    public int dwPanelVersion;				// 前面板版本,高16位是主版本,低16位是次版本
    public int dwHardwareVersion;	        // 硬件版本,高16位是主版本,低16位是次版本
    public byte byAlarmInPortNum;		//DVR报警输入个数
    public byte byAlarmOutPortNum;		//DVR报警输出个数
    public byte byRS232Num;			//DVR 232串口个数
    public byte byRS485Num;			//DVR 485串口个数
    public byte byNetworkPortNum;		//网络口个数
    public byte byDiskCtrlNum;			//DVR 硬盘控制器个数
    public byte byDiskNum;				//DVR 硬盘个数
    public byte byDVRType;				//DVR类型, 1:DVR 2:ATM DVR 3:DVS ......
    public byte byChanNum;				//DVR 通道个数
    public byte byStartChan;			//起始通道号,例如DVS-1,DVR - 1
    public byte byDecordChans;			//DVR 解码路数
    public byte byVGANum;				//VGA口的个数
    public byte byUSBNum;				//USB口的个数
    public byte byAuxoutNum;			//辅口的个数
    public byte byAudioNum;			        //语音口的个数
    public byte byIPChanNum;			//最大数字通道数


}
