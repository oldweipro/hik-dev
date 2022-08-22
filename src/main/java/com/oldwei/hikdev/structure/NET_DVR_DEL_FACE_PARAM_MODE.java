package com.oldwei.hikdev.structure;

import com.sun.jna.Union;

/**
 * @author oldwei
 * @date 2021-5-17 18:49
 */
public class NET_DVR_DEL_FACE_PARAM_MODE extends Union {
    /**
     * 联合体长度
     */
    public byte[] uLen = new byte[588];
    /**
     * 按卡号的方式删除
     */
    public NET_DVR_FACE_PARAM_BYCARD struByCard;
    /**
     * 按读卡器的方式删除
     */
    public NET_DVR_FACE_PARAM_BYREADER struByReader;
}
