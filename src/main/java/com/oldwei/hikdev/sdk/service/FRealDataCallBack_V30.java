package com.oldwei.hikdev.sdk.service;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.win32.StdCallLibrary;

/**
 * @author oldwei
 * @date 2021-5-19 19:21
 */
public interface FRealDataCallBack_V30 extends StdCallLibrary.StdCallCallback {
    /**
     * 预览回调
     * playControl是sdk播放控件
     * m_lPort是来判断是否预览成功0是成功，-1是失败
     *
     * @param lRealHandle
     * @param dwDataType
     * @param pBuffer
     * @param dwBufSize
     * @param pUser
     */
    void invoke(int lRealHandle, int dwDataType, ByteByReference pBuffer, int dwBufSize, Pointer pUser);
}
