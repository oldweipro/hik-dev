package com.oldwei.hikdev.constant;

/**
 * @author oldwei
 * @date 2021-5-12 15:11
 */
public interface HikConstant {
    /**
     * DVR本地登陆名
     */
    int MAX_NAMELEN = 16;
    int NET_DVR_DEV_ADDRESS_MAX_LEN = 129;
    int NET_DVR_LOGIN_USERNAME_MAX_LEN = 64;
    int NET_DVR_LOGIN_PASSWD_MAX_LEN = 64;
    /**
     * mac地址长度
     */
    int MACADDR_LEN = 6;
    /**
     * 序列号长度
     */
    int SERIALNO_LEN = 48;
    /**
     * 门禁卡号长度
     */
    int ACS_CARD_NO_LEN = 32;
    /**
     * 最大群组数
     */
    int MAX_GROUP_NUM_128 = 128;
    /**
     * 最大门数
     */
    int MAX_DOOR_NUM_256 = 256;
    /**
     * 卡密码长度
     */
    int CARD_PASSWORD_LEN = 8;
    /**
     * 用户名长度
     */
    int NAME_LEN = 32;

    int NET_SDK_CONFIG_STATUS_SUCCESS = 1000;
    int NET_SDK_CONFIG_STATUS_NEEDWAIT = 1001;
    int NET_SDK_CONFIG_STATUS_FINISH = 1002;
    int NET_SDK_CONFIG_STATUS_FAILED = 1003;
    int NET_SDK_CONFIG_STATUS_EXCEPTION = 1004;

    int NET_DVR_GET_CARD = 2560;
    int NET_DVR_SET_CARD = 2561;
    int NET_DVR_GET_FACE = 2566;
    int NET_DVR_SET_FACE = 2567;
    int NET_DVR_DEL_CARD = 2562;
    /**
     * 获取人脸参数
     */
    int NET_DVR_GET_FACE_PARAM_CFG = 2507;

    /**
     * 下发错误信息
     */
    int ERROR_MSG_LEN = 32;
    /**
     * 最大读卡器数
     */
    int MAX_CARD_READER_NUM_512 = 512;
    /**
     * 最大人脸数
     */
    int MAX_FACE_NUM = 2;
    /**
     * 删除人脸参数
     */
    int NET_DVR_DEL_FACE_PARAM_CFG = 2509;
    /**
     * 9000报警信息主动上传
     */
    int COMM_ALARM_V30 = 0x4000;
    int COMM_ALARM_V40 = 0x4007;
    /**
     * 行为分析信息上传
     */
    int COMM_ALARM_RULE = 0x1102;
    /**
     * 交通抓拍结果上传
     */
    int COMM_UPLOAD_PLATE_RESULT = 0x2800;
    /**
     * 交通抓拍的终端图片上传
     */
    int COMM_ITS_PLATE_RESULT = 0x3050;
    /**
     * 客流量统计报警上传
     */
    int COMM_ALARM_PDC = 0x1103;
    /**
     * 停车场数据上传
     */
    int COMM_ITS_PARK_VEHICLE = 0x3056;
    /**
     * 交通取证报警信息
     */
    int COMM_ALARM_TFS = 0x1113;
    /**
     * 交通事件报警信息扩展
     */
    int COMM_ALARM_AID_V41 = 0x1115;
    /**
     * 交通事件报警信息扩展
     */
    int COMM_ALARM_TPS_V41 = 0x1114;
    /**
     * 人脸识别结果上传
     */
    int COMM_UPLOAD_FACESNAP_RESULT = 0x1112;
    /**
     * 人脸比对结果上传
     */
    int COMM_SNAP_MATCH_ALARM = 0x2902;
    /**
     * 门禁主机报警信息
     */
    int COMM_ALARM_ACS = 0x5002;
    /**
     * 门禁身份证刷卡信息
     */
    int COMM_ID_INFO_ALARM = 0x5200;
    /**
     * 设备支持AI开放平台接入，上传视频检测数据
     */
    int COMM_UPLOAD_AIOP_VIDEO = 0x4021;
    /**
     * 设备支持AI开放平台接入，上传图片检测数据
     */
    int COMM_UPLOAD_AIOP_PICTURE = 0x4022;
    /**
     * ISAPI协议报警信息
     */
    int COMM_ISAPI_ALARM = 0x6009;

    /**
     * 最大32路模拟报警输出
     */
    int MAX_ANALOG_ALARMOUT = 32;
    /**
     * 允许加入的最多报警输出数
     */
    int MAX_IP_ALARMOUT = 64;
    /**
     * 96
     */
    int MAX_ALARMOUT_V30 = (MAX_ANALOG_ALARMOUT + MAX_IP_ALARMOUT);
    /***
     * 最大32个模拟通道
     */
    int MAX_ANALOG_CHANNUM = 32;
    /**
     * 允许加入的最多IP通道数
     */
    int MAX_IP_CHANNEL = 32;
    /**
     * 64
     */
    int MAX_CHANNUM_V30 = (MAX_ANALOG_CHANNUM + MAX_IP_CHANNEL);
    /**
     * 9000设备最大硬盘数/* 最多33个硬盘(包括16个内置SATA硬盘、1个eSATA硬盘和16个NFS盘)
     */
    int MAX_DISKNUM_V30 = 33;

    int VCA_MAX_POLYGON_POINT_NUM = 10;


    /**
     * 获取设备能力
     */
    int ISAPI_DATA_LEN = 1024 * 1024;
    int ISAPI_STATUS_LEN = 4 * 4096;
    int BYTE_ARRAY_LEN = 1024;


    int MAX_LICENSE_LEN = 16;
    /**
     * 车牌号最大长度
     */
    int MAX_LICENSE_LEN_EX = 32;

    int MAX_PARKNO_LEN = 16; //车位编号长度
    int MAX_ID_LEN = 48; //编号最大长度
    int ILLEGAL_LEN = 32; //违法代码长度
    int MONITORSITE_ID_LEN = 48;//监测点编号长度
    int DEVICE_ID_LEN = 48;
    int MAX_TPS_RULE = 8;     // 最大参数规则数目
    int MAX_HUMAN_BIRTHDATE_LEN = 10;//人员信息
    int MAX_ID_NUM_LEN = 32;  //最大身份证号长度
    int MAX_ID_NAME_LEN = 128;   //最大姓名长度
    int MAX_ID_ADDR_LEN = 280;   //最大住址长度
    int MAX_ID_ISSUING_AUTHORITY_LEN = 128; //最大签发机关长度
    int MAX_FILE_PATH_LEN = 256; //文件路径长度

    int NET_DVR_SYSHEAD = 1;//系统头数据
    int NET_DVR_STREAMDATA = 2;//视频流数据（包括复合流和音视频分开的视频流数据）

    int STREAME_REALTIME = 0;
    int STREAME_FILE = 1;

    int TEMPLATE_NAME_LEN = 32;      //计划模板名称长度
    int MAX_HOLIDAY_GROUP_NUM = 16;  //计划模板最大假日组数
    int NET_DVR_GET_CARD_RIGHT_WEEK_PLAN_V50 = 2304;  //获取卡权限周计划参数V50
    int NET_DVR_SET_CARD_RIGHT_WEEK_PLAN_V50 = 2305;  //设置卡权限周计划参数V50
    int NET_DVR_SET_CARD_RIGHT_PLAN_TEMPLATE_V50 = 2323;//设置卡权限计划模板参数V50
    int MAX_TIMESEGMENT_V30 = 8;    //9000设备最大时间段数
    int MAX_DAYS = 7;      //每周天数
}
