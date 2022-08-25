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

    boolean NET_DVR_StopRealPlay(int lRealHandle);

    int NET_DVR_RealPlay_V40(int lUserID, NET_DVR_PREVIEWINFO lpPreviewInfo, FRealDataCallBack_V30 fRealDataCallBack_V30, Pointer pUser);

    int NET_DVR_Login_V30(String sDVRIP, short wDVRPort, String sUserName, String sPassword, NET_DVR_DEVICEINFO_V30 lpDeviceInfo);

    boolean NET_DVR_SaveRealData(int lRealHandle, String sFileName);

    boolean NET_DVR_SetDeviceConfig(int lUserID, int dwCommand, int dwCount, Pointer lpInBuffer, int dwInBufferSize, Pointer lpStatusList, Pointer lpInParamBuffer, int dwInParamBufferSize);

    boolean NET_DVR_GetDeviceConfig(int lUserID, int dwCommand, int dwCount, Pointer lpInBuffer, int dwInBufferSize, Pointer lpStatusList, Pointer lpOutBuffer, int dwOutBufferSize);

    boolean NET_DVR_ControlGateway(int lUserID, int lGatewayIndex, int dwStaic);

    /**
     * 单帧数据捕获并保存成JPEG图。
     * lUserID
     * [in] NET_DVR_Login_V40等登录接口的返回值
     * lChannel
     * [in] 通道号
     * lpJpegPara
     * [in] JPEG图像参数
     * sPicFileName
     * [in] 保存JPEG图的文件路径（包括文件名）
     *
     * @param lUserID      NET_DVR_Login_V40等登录接口的返回值
     * @param lChannel     通道号
     * @param lpJpegPara   JPEG图像参数
     * @param sPicFileName 保存JPEG图的文件路径（包括文件名）
     * @return TRUE表示成功，FALSE表示失败。接口返回失败请调用NET_DVR_GetLastError获取错误码，通过错误码判断出错原因。
     */
    boolean NET_DVR_CaptureJPEGPicture(int lUserID, int lChannel, NET_DVR_JPEGPARA lpJpegPara, byte[] sPicFileName);

    /**
     * 单帧数据捕获并保存成JPEG存放在指定的内存空间中。
     * lUserID
     * [in] NET_DVR_Login_V40等登录接口的返回值
     * lChannel
     * [in] 通道号
     * lpJpegPara
     * [in] JPEG图像参数
     * sJpegPicBuffer
     * [in] 保存JPEG数据的缓冲区
     * dwPicSize
     * [in] 输入缓冲区大小
     * lpSizeReturned
     * [out] 返回图片数据的大小
     *
     * @param lUserID        NET_DVR_Login_V40等登录接口的返回值
     * @param lChannel       通道号
     * @param lpJpegPara     JPEG图像参数
     * @param sJpegPicBuffer 保存JPEG数据的缓冲区
     * @param dwPicSize      输入缓冲区大小
     * @param lpSizeReturned 返回图片数据的大小
     * @return TRUE表示成功，FALSE表示失败。接口返回失败请调用NET_DVR_GetLastError获取错误码，通过错误码判断出错原因。
     */
    boolean NET_DVR_CaptureJPEGPicture_NEW(int lUserID, int lChannel, NET_DVR_JPEGPARA lpJpegPara, byte[] sJpegPicBuffer, int dwPicSize, IntByReference lpSizeReturned);

    /**
     * bmp预览时，单帧数据捕获并保存成图片。
     * lRealHandle
     * [in] NET_DVR_RealPlay或NET_DVR_RealPlay_V30的返回值
     * sPicFileName
     * [in] 保存图象的文件路径（包括文件名）。路径长度和操作系统有关，sdk不做限制，windows默认路径长度小于等于256字节（包括文件名在内）。
     *
     * @param lRealHandle  NET_DVR_RealPlay或NET_DVR_RealPlay_V30的返回值
     * @param sPicFileName 保存图象的文件路径（包括文件名）。路径长度和操作系统有关，sdk不做限制，windows默认路径长度小于等于256字节（包括文件名在内）。
     * @return TRUE表示成功，FALSE表示失败。接口返回失败请调用NET_DVR_GetLastError获取错误码，通过错误码判断出错原因。
     */
    boolean NET_DVR_CapturePicture(int lRealHandle, String sPicFileName);

    /**
     * lUserID
     * [in] NET_DVR_Login_V40等登录接口的返回值
     * dwCommand
     * [in] 设备配置命令，详见“Remarks”说明
     * lChannel
     * [in] 通道号或者组号，不同的命令对应不同的取值，如果该参数无效则置为0xFFFFFFFF即可，详见“Remarks”说明
     * lpOutBuffer
     * [out] 接收数据的缓冲指针
     * dwOutBufferSize
     * [in] 接收数据的缓冲长度(以字节为单位)，不能为0
     * lpBytesReturned
     * [out] 实际收到的数据长度指针，不能为NULL
     *
     * @param lUserID         [in] NET_DVR_Login_V40等登录接口的返回值
     * @param dwCommand       [in] 设备配置命令，详见“Remarks”说明
     * @param lChannel        [in] 通道号或者组号，不同的命令对应不同的取值，如果该参数无效则置为0xFFFFFFFF即可，详见“Remarks”说明
     * @param lpOutBuffer     [out] 接收数据的缓冲指针
     * @param dwOutBufferSize [in] 接收数据的缓冲长度(以字节为单位)，不能为0
     * @param lpBytesReturned [out] 实际收到的数据长度指针，不能为NULL
     * @return
     */

    boolean NET_DVR_GetDVRConfig(int lUserID, int dwCommand, int lChannel, Pointer lpOutBuffer, int dwOutBufferSize, IntByReference lpBytesReturned);

    boolean NET_DVR_GetRtspConfig(int lUserID, int dwCommand, NET_DVR_RTSPCFG lpOutBuffer, int dwOutBufferSize);

    boolean NET_DVR_MatrixStartDynamic_V41(int lUserID, int dwDecChanNum, NET_DVR_PU_STREAM_CFG_V41 lpDynamicInfo);

}
