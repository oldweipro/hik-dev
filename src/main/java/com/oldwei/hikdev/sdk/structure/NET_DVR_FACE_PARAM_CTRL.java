package com.oldwei.hikdev.sdk.structure;

import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-17 18:04
 */
public class NET_DVR_FACE_PARAM_CTRL extends Structure {
    public int dwSize;
    /**
     * 删除方式，0-按卡号方式删除，1-按读卡器删除
     */
    public byte byMode;
    /**
     * 保留
     */
    public byte[] byRes1 = new byte[3];
    /**
     * 处理方式
     */
    public NET_DVR_DEL_FACE_PARAM_MODE struProcessMode;
    /**
     * 保留
     */
    public byte[] byRes = new byte[64];
}
