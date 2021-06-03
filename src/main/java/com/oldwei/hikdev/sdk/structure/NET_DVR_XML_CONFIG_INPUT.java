package com.oldwei.hikdev.sdk.structure;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class NET_DVR_XML_CONFIG_INPUT extends Structure {

    public int dwSize;
    public Pointer lpRequestUrl;
    public int dwRequestUrlLen;
    public Pointer lpInBuffer;
    public int dwInBufferSize;
    public int dwRecvTimeOut;
    public byte[] byRes = new byte[32];
}
