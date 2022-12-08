package com.oldwei.hikdev.service;

import com.sun.jna.Library;
import com.sun.jna.NativeLong;
import com.sun.jna.examples.win32.W32API;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.NativeLongByReference;

/**
 * @author oldwei
 * @date 2021-5-19 18:27
 */
public interface IHikPlayCtrlService extends Library {

    boolean PlayM4_GetPort(NativeLongByReference nPort);

    boolean PlayM4_OpenStream(NativeLong nPort, ByteByReference pFileHeadBuf, int nSize, int nBufPoolSize);

    boolean PlayM4_InputData(NativeLong nPort, ByteByReference pBuf, int nSize);

    boolean PlayM4_CloseStream(NativeLong nPort);

    boolean PlayM4_SetStreamOpenMode(NativeLong nPort, int nMode);

    boolean PlayM4_Play(NativeLong nPort, W32API.HWND hWnd);

    boolean PlayM4_Stop(NativeLong nPort);

    boolean PlayM4_SetSecretKey(NativeLong nPort, NativeLong lKeyType, String pSecretKey, NativeLong lKeyLen);

}
