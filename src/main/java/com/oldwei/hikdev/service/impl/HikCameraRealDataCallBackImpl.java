package com.oldwei.hikdev.service.impl;

import com.oldwei.hikdev.constant.HikConstant;
import com.oldwei.hikdev.service.FRealDataCallBack_V30;
import com.oldwei.hikdev.service.IHikPlayCtrlService;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.NativeLongByReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author oldwei
 * @date 2021-5-19 19:43
 */
@Slf4j
@RequiredArgsConstructor
public class HikCameraRealDataCallBackImpl implements FRealDataCallBack_V30 {
    private final IHikPlayCtrlService hikPlayCtrlService;

    @Override
    public void invoke(int lRealHandle, int dwDataType, ByteByReference pBuffer, int dwBufSize, Pointer pUser) {
        //TODO 这个值需要动态获取回调预览时播放库端口指针
        NativeLongByReference m_lPort = new NativeLongByReference(new NativeLong(0));
        switch (dwDataType) {
            //系统头
            case HikConstant.NET_DVR_SYSHEAD:
                //获取播放库未使用的通道号
                if (!this.hikPlayCtrlService.PlayM4_GetPort(m_lPort)) {
                    break;
                }
                if (dwBufSize > 0) {
                    //设置实时流播放模式
                    if (!this.hikPlayCtrlService.PlayM4_SetStreamOpenMode(m_lPort.getValue(), HikConstant.STREAME_REALTIME)) {
                        break;
                    }
                    //打开流接口
                    if (!this.hikPlayCtrlService.PlayM4_OpenStream(m_lPort.getValue(), pBuffer, dwBufSize, 1024 * 1024)) {
                        break;
                    }
                    //播放开始
                    if (!this.hikPlayCtrlService.PlayM4_Play(m_lPort.getValue(), null)) {
                        break;
                    }
                }
                //码流数据
            case HikConstant.NET_DVR_STREAMDATA:
                if (dwBufSize <= 0) {
                    log.info("没有数据");
                }
                if ((dwBufSize > 0) && (m_lPort.getValue().intValue() != -1)) {
                    //视频流数据
                    byte[] videoStreamData = pBuffer.getPointer().getByteArray(0, dwBufSize);
                    if (videoStreamData.length > 0) {
                        // 这里是流数据 videoStreamData，数据大小dwBufSize
                    }
                    //输入流数据
                    if (!this.hikPlayCtrlService.PlayM4_InputData(m_lPort.getValue(), pBuffer, dwBufSize)) {
                        break;
                    }
                }
            default:
        }
    }
}
