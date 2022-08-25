package com.oldwei.hikdev.structure;

import com.oldwei.hikdev.constant.HikConstant;
import com.sun.jna.Structure;
import lombok.ToString;

/**
 * @author sunyuchang
 * @date 2022-8-25 11:33
 */
@ToString
public class NET_DVR_DEVICECFG_V40 extends Structure {
    public int dwSize;
    public byte[] sDVRName = new byte[HikConstant.NAME_LEN];     //DVR名称
    public int dwDVRID;                //DVR ID,用于遥控器 //V1.4(0-99), V1.5(0-255)
    public int dwRecycleRecord;        //是否循环录像,0:不是; 1:是
    //以下不可更改
    public byte[] sSerialNumber = new byte[HikConstant.SERIALNO_LEN];  //序列号
    public int dwSoftwareVersion;            //软件版本号,高16位是主版本,低16位是次版本
    public int dwSoftwareBuildDate;            //软件生成日期,0xYYYYMMDD
    public int dwDSPSoftwareVersion;            //DSP软件版本,高16位是主版本,低16位是次版本
    public int dwDSPSoftwareBuildDate;        // DSP软件生成日期,0xYYYYMMDD
    public int dwPanelVersion;                // 前面板版本,高16位是主版本,低16位是次版本
    public int dwHardwareVersion;    // 硬件版本,高16位是主版本,低16位是次版本
    public byte byAlarmInPortNum;        //DVR报警输入个数
    public byte byAlarmOutPortNum;        //DVR报警输出个数
    public byte byRS232Num;            //DVR 232串口个数
    public byte byRS485Num;            //DVR 485串口个数
    public byte byNetworkPortNum;        //网络口个数
    public byte byDiskCtrlNum;            //DVR 硬盘控制器个数
    public byte byDiskNum;                //DVR 硬盘个数
    public byte byDVRType;                //DVR类型, 1:DVR 2:ATM DVR 3:DVS ......
    public byte byChanNum;                //DVR 通道个数
    public byte byStartChan;            //起始通道号,例如DVS-1,DVR - 1
    public byte byDecordChans;            //DVR 解码路数
    public byte byVGANum;                //VGA口的个数
    public byte byUSBNum;                //USB口的个数
    public byte byAuxoutNum;            //辅口的个数
    public byte byAudioNum;            //语音口的个数
    public byte byIPChanNum;            //最大数字通道数 低8位，高8位见byHighIPChanNum
    public byte byZeroChanNum;            //零通道编码个数
    public byte bySupport;        //能力，位与结果为0表示不支持，1表示支持，
    public byte byEsataUseage;        //Esata的默认用途，0-默认备份，1-默认录像
    public byte byIPCPlug;            //0-关闭即插即用，1-打开即插即用
    public byte byStorageMode;        //0-盘组模式,1-磁盘配额, 2抽帧模式, 3-自动
    public byte bySupport1;        //能力，位与结果为0表示不支持，1表示支持
    public short wDevType;//设备型号
    public byte[] byDevTypeName = new byte[HikConstant.DEV_TYPE_NAME_LEN];//设备型号名称
    public byte bySupport2; //能力集扩展，位与结果为0表示不支持，1表示支持
    //bySupport2 & 0x1, 表示是否支持扩展的OSD字符叠加(终端和抓拍机扩展区分)
    public byte byAnalogAlarmInPortNum; //模拟报警输入个数
    public byte byStartAlarmInNo;    //模拟报警输入起始号
    public byte byStartAlarmOutNo;  //模拟报警输出起始号
    public byte byStartIPAlarmInNo;  //IP报警输入起始号
    public byte byStartIPAlarmOutNo; //IP报警输出起始号
    public byte byHighIPChanNum;      //数字通道个数，高8位
    public byte byEnableRemotePowerOn;//是否启用在设备休眠的状态下远程开机功能，0-不启用，1-启用
    public short wDevClass; //设备大类备是属于哪个产品线，0 保留，1-50 DVR，51-100 DVS，101-150 NVR，151-200 IPC，65534 其他，具体分类方法见《设备类型对应序列号和类型值.docx》
    public byte[] byRes2 = new byte[6];    //保留
}
