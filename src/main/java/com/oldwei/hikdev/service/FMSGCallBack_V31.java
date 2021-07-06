package com.oldwei.hikdev.service;

import com.oldwei.hikdev.structure.NET_DVR_ALARMER;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary;

/**
 * FMSGCallBack_V31
 * @author oldwei
 * @date 2021-5-18 15:01
 */
public interface FMSGCallBack_V31 extends StdCallLibrary.StdCallCallback {
    boolean invoke(int lCommand, NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser);
}
