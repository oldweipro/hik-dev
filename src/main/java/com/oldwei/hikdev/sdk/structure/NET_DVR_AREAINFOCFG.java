package com.oldwei.hikdev.sdk.structure;

import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 13:35
 */
public class NET_DVR_AREAINFOCFG extends Structure {
    public short wNationalityID; //国籍
    public short wProvinceID; //省
    public short wCityID; //市
    public short wCountyID; //县
    public int dwCode; //国家标准的省份、城市、县级代码，当这个字段不为0的时候，使用这个值，新设备上传这个值表示籍贯参数，老设备这个值为0
}
