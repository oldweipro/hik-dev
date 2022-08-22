package com.oldwei.hikdev.structure;

import com.sun.jna.Union;

public class UNION_PDC_STATPARAM extends Union {
    // public byte[] byLen = new byte[140];
    public NET_DVR_STATFRAME struStatFrame;
    public NET_DVR_STATTIME struStatTime;
}