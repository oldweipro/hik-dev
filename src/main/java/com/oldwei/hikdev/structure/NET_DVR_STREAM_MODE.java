package com.oldwei.hikdev.structure;

import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2022/8/24 19:12
 */
public class NET_DVR_STREAM_MODE extends Structure {

    public byte  byGetStreamType;//取流方式：0- 直接从设备取流；1- 从流媒体取流；2- 通过IPServer获得IP地址后取流；
    //3- 通过IPServer找到设备，再通过流媒体取设备的流； 4- 通过流媒体由URL去取流；5- 通过hiDDNS域名连接设备然后从设备取流
    public byte[] byRes= new byte[3];//保留，置为0
    public NET_DVR_GET_STREAM_UNION uGetStream =new NET_DVR_GET_STREAM_UNION();//不同取流方式联合体

    public void read(){
        super.read();
        switch(byGetStreamType)
        {
            case 0:
                uGetStream.setType(NET_DVR_IPCHANINFO.class);
                break;
            case 6:
                uGetStream.setType(NET_DVR_IPCHANINFO_V40.class);
                break;
            default:
                break;
        }
    }
}
