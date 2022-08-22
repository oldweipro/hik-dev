package com.oldwei.hikdev.structure;

import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 13:09
 */
public class NET_DVR_IPADDR extends Structure {
    public byte[] sIpV4 = new byte[16];
    public byte[] byRes = new byte[128];

    @Override
    public String toString() {
        return "NET_DVR_IPADDR.sIpV4: " + new String(sIpV4) + "\n" + "NET_DVR_IPADDR.byRes: " + new String(byRes) + "\n";
    }
}
