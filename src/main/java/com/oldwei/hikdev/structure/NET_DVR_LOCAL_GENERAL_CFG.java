package com.oldwei.hikdev.structure;

import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 14:49
 */
public class NET_DVR_LOCAL_GENERAL_CFG extends Structure {
    public byte byExceptionCbDirectly;    //0-通过线程池异常回调，1-直接异常回调给上层
    public byte byNotSplitRecordFile;     //回放和预览中保存到本地录像文件不切片 0-默认切片，1-不切片
    public byte byResumeUpgradeEnable;    //断网续传升级使能，0-关闭（默认），1-开启
    public byte byAlarmJsonPictureSeparate;   //控制JSON透传报警数据和图片是否分离，0-不分离，1-分离（分离后走COMM_ISAPI_ALARM回调返回）
    public byte[] byRes = new byte[4];      //保留
    public long i64FileSize;      //单位：Byte
    public int dwResumeUpgradeTimeout;       //断网续传重连超时时间，单位毫秒
    public byte[] byRes1 = new byte[236];    //预留
}
