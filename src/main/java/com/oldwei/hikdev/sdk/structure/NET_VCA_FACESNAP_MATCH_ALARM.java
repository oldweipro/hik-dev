package com.oldwei.hikdev.sdk.structure;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 13:33
 */
public class NET_VCA_FACESNAP_MATCH_ALARM extends Structure {
    public int dwSize;             // 结构大小
    public float fSimilarity; //相似度，[0.001,1]
    public NET_VCA_FACESNAP_INFO_ALARM struSnapInfo = new NET_VCA_FACESNAP_INFO_ALARM(); //抓拍信息
    public NET_VCA_BLOCKLIST_INFO_ALARM struBlockListInfo = new NET_VCA_BLOCKLIST_INFO_ALARM(); //禁止名单信息
    public byte[] sStorageIP = new byte[16];        //存储服务IP地址
    public short wStoragePort;            //存储服务端口号
    public byte byMatchPicNum; //匹配图片的数量，0-保留（老设备这个值默认0，新设备这个值为0时表示后续没有匹配的图片信息）
    public byte byPicTransType;//图片数据传输方式: 0-二进制；1-url
    public int dwSnapPicLen;//设备识别抓拍图片长度
    public Pointer pSnapPicBuffer;//设备识别抓拍图片指针
    public NET_VCA_RECT struRegion = new NET_VCA_RECT();//目标边界框，设备识别抓拍图片中，人脸子图坐标
    public int dwModelDataLen;//建模数据长度
    public Pointer pModelDataBuffer;// 建模数据指针
    public byte byModelingStatus;// 建模状态
    public byte byLivenessDetectionStatus;//活体检测状态：0-保留，1-未知（检测失败），2-非真人人脸，3-真人人脸，4-未开启活体检测
    public byte cTimeDifferenceH;         /*与UTC的时差（小时），-12 ... +14， +表示东区,0xff无效*/
    public byte cTimeDifferenceM;      	/*与UTC的时差（分钟），-30, 30, 45， +表示东区，0xff无效*/
    public byte byMask;                //抓拍图是否戴口罩，0-保留，1-未知，2-不戴口罩，3-戴口罩
    public byte bySmile;               //抓拍图是否微笑，0-保留，1-未知，2-不微笑，3-微笑
    public byte byContrastStatus;      //比对结果，0-保留，1-比对成功，2-比对失败
    public byte byBrokenNetHttp;     //断网续传标志位，0-不是重传数据，1-重传数据
}
