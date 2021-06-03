package com.oldwei.hikdev.sdk.structure;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class NET_DVR_JSON_DATA_CFG extends Structure  {
    //结构体大小
    public int dwSize;
    //JSON报文
    public Pointer lpJsonData;
    //JSON报文大小
    public int dwJsonDataSize;
    //图片内容
    public Pointer lpPicData;
    //图片内容大小
    public int dwPicDataSize;
    //红外人脸图片数据缓存
    public int lpInfraredFacePicBuffer;
    //红外人脸图片数据大小，等于0时，代表无人脸图片数据(当JSON报文为当ResponseStatus（JSON）报文时，该字段无意义；当Inbound Data（JSON）报文中没有infraredFaceURL时，该字段需要带上二进制图片内容）
    public Pointer dwInfraredFacePicSize;
    public byte[] byRes = new byte[248];
}
