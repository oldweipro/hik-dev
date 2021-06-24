package com.oldwei.hikdev.sdk.structure;

import com.oldwei.hikdev.constant.HikConstant;
import com.sun.jna.Structure;

/**
 * NET_DVR_Login_V30()参数结构
 *
 * @author oldwei
 * @date 2021-5-12 15:16
 */
public class NET_DVR_DEVICEINFO_V30 extends Structure {
    public byte[] sSerialNumber = new byte[HikConstant.SERIALNO_LEN];  //序列号
    public byte byAlarmInPortNum;    //报警输入个数
    public byte byAlarmOutPortNum;   //报警输出个数
    public byte byDiskNum;           //硬盘个数
    public byte byDVRType;         //设备类型, 1:DVR 2:ATM DVR 3:DVS ......
    public byte byChanNum;         //模拟通道个数
    public byte byStartChan;      //起始通道号,例如DVS-1,DVR - 1
    public byte byAudioChanNum;    //语音通道数
    public byte byIPChanNum;     //最大数字通道个数，低位
    public byte byZeroChanNum;    //零通道编码个数 //2010-01-16
    public byte byMainProto;      //主码流传输协议类型 0-private, 1-rtsp,2-同时支持private和rtsp
    public byte bySubProto;        //子码流传输协议类型0-private, 1-rtsp,2-同时支持private和rtsp
    public byte bySupport;        //能力，位与结果为0表示不支持，1表示支持，
    public byte bySupport1;        // 能力集扩充，位与结果为0表示不支持，1表示支持
    public byte bySupport2; /*能力*/
    public short wDevType;              //设备型号
    public byte bySupport3; //能力集扩展
    public byte byMultiStreamProto;//是否支持多码流,按位表示,0-不支持,1-支持,bit1-码流3,bit2-码流4,bit7-主码流，bit-8子码流
    public byte byStartDChan;        //起始数字通道号,0表示无效
    public byte byStartDTalkChan;    //起始数字对讲通道号，区别于模拟对讲通道号，0表示无效
    public byte byHighDChanNum;        //数字通道个数，高位
    public byte bySupport4;        //能力集扩展
    public byte byLanguageType;// 支持语种能力,按位表示,每一位0-不支持,1-支持
    //  byLanguageType 等于0 表示 老设备
    //  byLanguageType & 0x1表示支持中文
    //  byLanguageType & 0x2表示支持英文
    public byte byVoiceInChanNum;   //音频输入通道数
    public byte byStartVoiceInChanNo; //音频输入起始通道号 0表示无效
    public byte bySupport5;
    public byte bySupport6;   //能力
    public byte byMirrorChanNum;    //镜像通道个数，<录播主机中用于表示导播通道>
    public short wStartMirrorChanNo;  //起始镜像通道号
    public byte bySupport7;   //能力
    public byte byRes2;        //保留
}
