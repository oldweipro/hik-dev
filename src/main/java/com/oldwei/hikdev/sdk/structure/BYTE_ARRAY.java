package com.oldwei.hikdev.sdk.structure;

import com.sun.jna.Structure;

/**
 * @author zhangjie
 */
public class BYTE_ARRAY extends Structure {

    public byte[] byValue;

    public BYTE_ARRAY(int iLen) {
        byValue = new byte[iLen];
    }
}
