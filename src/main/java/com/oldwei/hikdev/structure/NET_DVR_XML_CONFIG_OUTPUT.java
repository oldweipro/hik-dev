package com.oldwei.hikdev.structure;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class NET_DVR_XML_CONFIG_OUTPUT extends Structure {

    public int dwSize;
    public Pointer lpOutBuffer;
    public int dwOutBufferSize;
    public int dwReturnedXMLSize;
    public Pointer lpStatusBuffer;
    public int dwStatusSize;
    public byte[] byRes = new byte[32];
}
