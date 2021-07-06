package com.oldwei.hikdev.structure;

import com.oldwei.hikdev.constant.HikConstant;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 13:12
 */
public class NET_DVR_PLATE_INFO extends Structure {
    /**
     * 车牌类型
     */
    public byte byPlateType;
    /**
     * 车牌颜色
     */
    public byte byColor;
    /**
     * 车牌亮度
     */
    public byte byBright;
    /**
     * 车牌字符个数
     */
    public byte byLicenseLen;
    /**
     * 整个车牌的置信度，-100
     */
    public byte byEntireBelieve;
    /**
     * 区域索引值 0-保留，1-欧洲(EU)，2-俄语区域(ER)，3-欧洲&俄罗斯(EU&CIS) ,4-中东(ME),0xff-所有
     */
    public byte byRegion;
    /**
     * 国家索引值，参照枚举COUNTRY_INDEX（不支持"COUNTRY_ALL = 0xff, //ALL  全部"）
     */
    public byte byCountry;
    /**
     * 区域（省份），各国家内部区域枚举，阿联酋参照 EMI_AREA
     */
    public byte byArea;
    /**
     * 车牌尺寸，0~未知，1~long, 2~short(中东车牌使用)
     */
    public byte byPlateSize;
    public byte byAddInfoFlag;
    /**
     * 国家/地区索引，索引值参考_CR_ INDEX_
     */
    public short wCRIndex;//
    /**
     * 保留
     */
    public byte[] byRes = new byte[12];
    /**
     * 车牌附加信息, 即中东车牌中车牌号码旁边的小字信息，(目前只有中东地区支持)
     */
    public byte[] sPlateCategory = new byte[8];
    /**
     * XML报警信息长度
     */
    public int dwXmlLen;
    /**
     * XML报警信息指针,报警类型为 COMM_ITS_PLATE_RESUL时有效，其XML对应到EventNotificationAlert XML Block
     */
    public Pointer pXmlBuf;
    /**
     * 车牌位置
     */
    public NET_VCA_RECT struPlateRect = new NET_VCA_RECT();
    /**
     * 车牌号码,注：中东车牌需求把小字也纳入车牌号码，小字和车牌号中间用空格分隔
     */
    public byte[] sLicense = new byte[HikConstant.MAX_LICENSE_LEN];
    /**
     * 各个识别字符的置信度，如检测到车牌"浙A12345", 置信度为,20,30,40,50,60,70，则表示"浙"字正确的可能性只有%，"A"字的正确的可能性是%
     */
    public byte[] byBelieve = new byte[HikConstant.MAX_LICENSE_LEN];
}
