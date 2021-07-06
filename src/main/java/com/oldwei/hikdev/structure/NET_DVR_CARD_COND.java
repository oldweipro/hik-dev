package com.oldwei.hikdev.structure;

import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-13 10:26
 */
public class NET_DVR_CARD_COND extends Structure {
    public int dwSize;
    /**
     * 设置或获取卡数量，获取时置为0xffffffff表示获取所有卡信息
     */
    public int dwCardNum;
    public byte[] byRes = new byte[64];
}
