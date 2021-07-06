package com.oldwei.hikdev.service;

import com.oldwei.hikdev.structure.*;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

/**
 * @author oldwei
 * @date 2021-5-12 15:48
 */
public interface IHikDevService extends StdCallLibrary {

    boolean NET_DVR_Init();

    boolean NET_DVR_Cleanup();

    boolean NET_DVR_Logout(int lUserID);

    int NET_DVR_Login_V40(NET_DVR_USER_LOGIN_INFO pLoginInfo, NET_DVR_DEVICEINFO_V40 lpDeviceInfo);

    int NET_DVR_GetLastError();

    int NET_DVR_StartRemoteConfig(int lUserID, int dwCommand, Pointer lpInBuffer, int dwInBufferLen, FRemoteConfigCallBack cbStateCallBack, Pointer pUserData);

    int NET_DVR_GetNextRemoteConfig(int lHandle, Pointer lpOutBuff, int dwOutBuffSize);

    boolean NET_DVR_StopRemoteConfig(int lHandle);

    int NET_DVR_SendWithRecvRemoteConfig(int lHandle, Pointer lpInBuff, int dwInBuffSize, Pointer lpOutBuff, int dwOutBuffSize, IntByReference dwOutDataLen);

    boolean NET_DVR_RemoteControl(int lUserID, int dwCommand, Pointer lpInBuffer, int dwInBufferSize);

    boolean NET_DVR_SetSDKLocalCfg(int enumType, Pointer lpInBuff);

    boolean NET_DVR_SetDVRMessageCallBack_V31(FMSGCallBack_V31 fMessageCallBack, Pointer pUser);

    int NET_DVR_SetupAlarmChan_V41(int lUserID, NET_DVR_SETUPALARM_PARAM lpSetupParam);

    boolean NET_DVR_CloseAlarmChan_V30(int lAlarmHandle);

    int NET_DVR_StartListen_V30(String sLocalIP, short wLocalPort, FMSGCallBack dataCallback, Pointer pUserData);

    boolean NET_DVR_StopListen_V30(int lListenHandle);

    boolean NET_DVR_STDXMLConfig(int lUserID, NET_DVR_XML_CONFIG_INPUT lpInputParam, NET_DVR_XML_CONFIG_OUTPUT lpOutputParam);

    int NET_DVR_RealPlay_V30(int lUserID, NET_DVR_CLIENTINFO lpClientInfo, FRealDataCallBack_V30 fRealDataCallBack_V30, Pointer pUser, boolean bBlocked);

    boolean  NET_DVR_StopRealPlay(int lRealHandle);

    int NET_DVR_RealPlay_V40(int lUserID, NET_DVR_PREVIEWINFO lpPreviewInfo, FRealDataCallBack_V30 fRealDataCallBack_V30, Pointer pUser);

    int NET_DVR_Login_V30(String sDVRIP, short wDVRPort, String sUserName, String sPassword, NET_DVR_DEVICEINFO_V30 lpDeviceInfo);

    boolean NET_DVR_SaveRealData(int lRealHandle, String sFileName);

}
