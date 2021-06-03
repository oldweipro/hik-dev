package com.oldwei.hikdev.sdk.structure;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 13:17
 */
public class NET_ITS_PLATE_RESULT extends Structure {
    public int dwSize;
    public int dwMatchNo;
    public byte byGroupNum;
    public byte byPicNo;
    public byte bySecondCam;
    public byte byFeaturePicNo;
    public byte byDriveChan;
    public byte byVehicleType;
    public byte byDetSceneID;
    public byte byVehicleAttribute;
    public short wIllegalType;
    public byte[] byIllegalSubType = new byte[8];
    public byte byPostPicNo;
    public byte byChanIndex;
    public short wSpeedLimit;
    /**
     * byChanIndexEx*256+byChanIndex表示真实通道号。
     */
    public byte byChanIndexEx;
    public byte byRes2;
    public NET_DVR_PLATE_INFO struPlateInfo = new NET_DVR_PLATE_INFO();
    public NET_DVR_VEHICLE_INFO struVehicleInfo = new NET_DVR_VEHICLE_INFO();
    public byte[] byMonitoringSiteID = new byte[48];
    public byte[] byDeviceID = new byte[48];
    public byte byDir;
    public byte byDetectType;
    public byte byRelaLaneDirectionType;
    public byte byCarDirectionType;
    public int dwCustomIllegalType;
    public Pointer pIllegalInfoBuf;
    public byte byIllegalFromatType;
    public byte byPendant;
    public byte byDataAnalysis;
    public byte byYellowLabelCar;
    public byte byDangerousVehicles;
    public byte byPilotSafebelt;
    public byte byCopilotSafebelt;
    public byte byPilotSunVisor;
    public byte byCopilotSunVisor;
    public byte byPilotCall;
    public byte byBarrierGateCtrlType;
    public byte byAlarmDataType;
    public NET_DVR_TIME_V30 struSnapFirstPicTime = new NET_DVR_TIME_V30();
    public int dwIllegalTime;
    public int dwPicNum;
    public NET_ITS_PICTURE_INFO[] struPicInfo = new NET_ITS_PICTURE_INFO[6];
}
