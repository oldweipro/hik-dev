package com.oldwei.hikdev.sdk.structure;

import com.oldwei.hikdev.sdk.service.HCNetSDK;
import com.sun.jna.Structure;

/**
 * 通道录像参数配置
 *
 * @author oldwei
 * @date 2021-5-18 13:19
 */
public class NET_DVR_PDC_ALRAM_INFO extends Structure {

    public int dwSize;
    /**
     * 0-单帧统计结果，1-最小时间段统计结果
     */
    public byte byMode;
    public byte byChannel;
    /**
     * 专业智能返回0，Smart 返回 1
     */
    public byte bySmart;
    /**
     * 保留字节
     */
    public byte byRes1;
    /**
     * 前端设备信息
     */
    public HCNetSDK.NET_VCA_DEV_INFO struDevInfo = new HCNetSDK.NET_VCA_DEV_INFO();
    public HCNetSDK.UNION_PDC_STATPARAM uStatModeParam = new HCNetSDK.UNION_PDC_STATPARAM();
    /**
     * 离开人数
     */
    public int dwLeaveNum;
    /**
     * 进入人数
     */
    public int dwEnterNum;
    /**
     * 断网续传标志位，0-不是重传数据，1-重传数据
     */
    public byte byBrokenNetHttp;
    public byte byRes3;
    /**
     * 与NET_VCA_DEV_INFO里的byIvmsChannel含义相同，能表示更大的值。老客户端用byIvmsChannel能继续兼容，但是最大到255。新客户端版本请使用wDevInfoIvmsChannelEx
     */
    public short wDevInfoIvmsChannelEx;
    /**
     * 经过人数（进入区域后徘徊没有触发进入、离开的人数）
     */
    public int dwPassingNum;
    public byte[] byRes2 = new byte[32];

    @Override
    public void read() {
        super.read();
        switch (byMode) {
            case 0:
                uStatModeParam.setType(HCNetSDK.NET_DVR_STATFRAME.class);
                break;
            case 1:
                uStatModeParam.setType(HCNetSDK.NET_DVR_STATTIME.class);
                break;
            default:
                break;
        }
        uStatModeParam.read();
    }

    @Override
    public void write() {
        super.write();
        uStatModeParam.write();
    }
}
