package com.oldwei.hikdev.structure;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 13:11
 */
public class NET_DVR_PLATE_RESULT extends Structure {
    public int dwSize;
    public byte byResultType;
    public byte byChanIndex;
    public short wAlarmRecordID;
    public int dwRelativeTime;
    public byte[] byAbsTime = new byte[32];
    public int dwPicLen;
    public int dwPicPlateLen;
    public int dwVideoLen;
    public byte byTrafficLight;
    public byte byPicNum;
    public byte byDriveChan;
    public byte byVehicleType;
    public int dwBinPicLen;
    public int dwCarPicLen;
    public int dwFarCarPicLen;
    public Pointer pBuffer3;
    public Pointer pBuffer4;
    public Pointer pBuffer5;
    public byte[] byRes3 = new byte[8];
    public NET_DVR_PLATE_INFO struPlateInfo;
    public NET_DVR_VEHICLE_INFO struVehicleInfo;
    public Pointer pBuffer1;
    public Pointer pBuffer2;
}
