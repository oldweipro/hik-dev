package com.oldwei.hikdev.structure;

import com.oldwei.hikdev.constant.HikConstant;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * 交通取证报警
 * @author oldwei
 * @date 2021-5-18 13:25
 */
public class NET_DVR_TFS_ALARM extends Structure {
    public int dwSize;                //结构体大小
    public int dwRelativeTime;        //相对时标
    public int dwAbsTime;               //绝对时标
    public int dwIllegalType;         //违章类型，采用国标定义，当dwIllegalType值为0xffffffff时使用byIllegalCode
    public int dwIllegalDuration;     //违法持续时间（单位：秒） = 抓拍最后一张图片的时间 - 抓拍第一张图片的时间
    public byte[] byMonitoringSiteID = new byte[HikConstant.MONITORSITE_ID_LEN];//监测点编号（路口编号、内部编号）
    public byte[] byDeviceID = new byte[HikConstant.DEVICE_ID_LEN];             //设备编号
    public NET_VCA_DEV_INFO struDevInfo = new NET_VCA_DEV_INFO();           //前端设备信息
    public NET_DVR_SCENE_INFO struSceneInfo = new NET_DVR_SCENE_INFO();         //场景信息
    public NET_DVR_TIME_EX struBeginRecTime = new NET_DVR_TIME_EX();      //录像开始时间
    public NET_DVR_TIME_EX struEndRecTime = new NET_DVR_TIME_EX();        //录像结束时间
    public NET_DVR_AID_INFO struAIDInfo = new NET_DVR_AID_INFO();           //交通事件信息
    public NET_DVR_PLATE_INFO struPlateInfo = new NET_DVR_PLATE_INFO();         //车牌信息
    public NET_DVR_VEHICLE_INFO struVehicleInfo = new NET_DVR_VEHICLE_INFO();       //车辆信息
    public int dwPicNum; //图片数量
    public NET_ITS_PICTURE_INFO[] struPicInfo = new NET_ITS_PICTURE_INFO[8];        //图片信息，最多8张
    public byte bySpecificVehicleType;     //具体车辆种类  参考识别结果类型VTR_RESULT
    public byte byLaneNo;  //关联车道号
    public byte[] byRes1 = new byte[2]; //保留
    public NET_DVR_TIME_V30 struTime = new NET_DVR_TIME_V30();//手动跟踪定位，当前时间。
    public int dwSerialNo;//序号；
    public byte byVehicleAttribute;//车辆属性，按位表示，0- 无附加属性(普通车)，bit1- 黄标车(类似年检的标志)，bit2- 危险品车辆，值：0- 否，1- 是
    public byte byPilotSafebelt;//0-表示未知,1-系安全带,2-不系安全带
    public byte byCopilotSafebelt;//0-表示未知,1-系安全带,2-不系安全带
    public byte byPilotSunVisor;//0-表示未知,1-不打开遮阳板,2-打开遮阳板
    public byte byCopilotSunVisor;//0-表示未知, 1-不打开遮阳板,2-打开遮阳板
    public byte byPilotCall;// 0-表示未知, 1-不打电话,2-打电话
    public byte[] byRes2 = new byte[2]; //保留
    public byte[] byIllegalCode = new byte[HikConstant.ILLEGAL_LEN/*32*/];//违法代码扩展，当dwIllegalType值为0xffffffff；使用这个值
    public short wCountry; // 国家索引值,参照枚举COUNTRY_INDEX
    public byte byRegion; //区域索引值,0-保留，1-欧洲(Europe Region)，2-俄语区域(Russian Region)，3-欧洲&俄罗斯(EU&CIS) , 4-中东（Middle East），0xff-所有
    public byte byCrossLine;//是否压线停车（侧方停车），0-表示未知，1-不压线，2-压线
    public byte[] byParkingSerialNO = new byte[16];//泊车位编号
    public byte byCrossSpaces;//是否跨泊车位停车（侧方停车），0-表示未知，1-未跨泊车位停车，2-跨泊车位停车
    public byte byAngledParking;//是否倾斜停车（侧方停车）, 0-表示未知，1-未倾斜停车，2-倾斜停车
    public byte byAlarmValidity;//报警置信度，可以输出驶入驶出的置信度，范围0-100；置信度越高，事件真实性越高
    public byte byDoorsStatus;//车门状态 0-车门关闭 1-车门开启
    public int dwXmlLen;//XML报警信息长度
    public Pointer pXmlBuf; // XML报警信息指针,其XML对应到EventNotificationAlert XML Block
    public byte byVehicleHeadTailStatus;//车头车尾状态 0-保留 1-车头 2-车尾
    public byte[] byRes = new byte[31]; //保留
}
