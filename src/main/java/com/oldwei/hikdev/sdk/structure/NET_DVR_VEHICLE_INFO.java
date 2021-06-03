package com.oldwei.hikdev.sdk.structure;

import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 13:14
 */
public class NET_DVR_VEHICLE_INFO extends Structure {
    /**
     * 车辆序号
     */
    public int dwIndex;
    /**
     * 车辆类型 0 表示其它车型，1 表示小型车，2 表示大型车 ,3表示行人触发 ,4表示二轮车触发 5表示三轮车触发(3.5Ver)
     */
    public byte byVehicleType;
    /**
     * 车身颜色深浅
     */
    public byte byColorDepth;
    /**
     * 车身颜色,参考VCR_CLR_CLASS
     */
    public byte byColor;
    /**
     * 雷达异常状态：
     * 0~雷达正常，
     * 1~雷达故障
     * 2~雷达一直发送某一个相同速度值
     * 3~雷达送出数据为0
     * 4~雷达送出数据过大或者过小
     */
    public byte byRadarState;
    /**
     * 单位km/h
     */
    public short wSpeed;
    /**
     * 前一辆车的车身长度
     */
    public short wLength;
    /**
     * 违规类型，0-正常，1-低速，2-超速，3-逆行，4-闯红灯,5-压车道线,6-不按导向，7-路口滞留，
     * 8-机占非，9-违法变道，10-不按车道 11-违反禁令，12-路口停车，13-绿灯停车, 14-未礼让行人(违法代码1357),
     * 15-违章停车，16-违章掉头,17-占用应急车道,18-禁右,19-禁左,20-压黄线,21-未系安全带,22-行人闯红灯,23-加塞,24-违法使用远光灯，
     * 25-驾驶时拨打接听手持电话，26-左转不让直行，27-右转不让左转，28-掉头不让直行，29-大弯小转, 30-闯绿灯，31-未带头盔，
     * 32-非机动车载人，33-非机动车占用机动车道，34-非机动车打伞棚, 35-黑烟车, 36-鸣笛
     */
    public byte byIllegalType;
    /**
     * 参考枚举类型 VLR_VEHICLE_CLASS
     */
    public byte byVehicleLogoRecog;
    /**
     * 车辆品牌子类型识别；参考VSB_VOLKSWAGEN_CLASS等子类型枚举。
     */
    public byte byVehicleSubLogoRecog;
    /**
     * 车辆子品牌年款，0-未知，参考"车辆子品牌年款.xlsx"
     */
    public byte byVehicleModel;
    /**
     * 自定义信息
     */
    public byte[] byCustomInfo = new byte[16];
    /**
     * 车辆主品牌，参考"车辆主品牌.xlsx" (该字段兼容byVehicleLogoRecog);
     */
    public short wVehicleLogoRecog;
    /**
     * 是否停车 0-无效，1-停车，2-未停车
     */
    public byte byIsParking;
    /**
     * 保留字节
     */
    public byte byRes;
    /**
     * 停车时间，单位：s
     */
    public int dwParkingTime;
    public byte[] byRes3 = new byte[8];
}
