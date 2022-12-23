package com.oldwei.hikdev.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.oldwei.hikdev.constant.HikConstant;
import com.oldwei.hikdev.entity.HikDevResponse;
import com.oldwei.hikdev.entity.config.DeviceHandleDTO;
import com.oldwei.hikdev.mqtt.MqttConnectClient;
import com.oldwei.hikdev.service.FMSGCallBack_V31;
import com.oldwei.hikdev.service.IHikAlarmDataService;
import com.oldwei.hikdev.service.IHikCardService;
import com.oldwei.hikdev.service.IHikDevService;
import com.oldwei.hikdev.structure.*;
import com.oldwei.hikdev.component.FileStream;
import com.oldwei.hikdev.util.ConfigJsonUtil;
import com.sun.jna.Pointer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import cn.hutool.core.codec.Base64;

import java.io.*;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author oldwei
 * @date 2021-5-18 12:05
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HikAlarmDataServiceImpl implements IHikAlarmDataService, FMSGCallBack_V31 {
    @Override
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    private String projectId = "000000";

    private final IHikDevService hikDevService;

    private final FileStream fileStream;

    private final IHikCardService hikCardService;

    private final MqttConnectClient mqttConnectClient;

    @Override
    public boolean invoke(int lCommand, NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
        this.alarmDataHandle(lCommand, pAlarmer, pAlarmInfo, dwBufLen, pUser);
        return true;
    }

    @Override
    public HikDevResponse setupAlarmChan(String ip) {
        HikDevResponse result = new HikDevResponse();
        NET_DVR_LOCAL_GENERAL_CFG struGeneralCfg = new NET_DVR_LOCAL_GENERAL_CFG();
        // 控制JSON透传报警数据和图片是否分离，0-不分离，1-分离（分离后走COMM_ISAPI_ALARM回调返回）
        struGeneralCfg.byAlarmJsonPictureSeparate = 1;
        struGeneralCfg.write();

        if (!this.hikDevService.NET_DVR_SetSDKLocalCfg(17, struGeneralCfg.getPointer())) {
            // log.info("NET_DVR_SetSDKLocalCfg失败");
            return result.err("NET_DVR_StopRemoteConfig接口调用失败，错误码：" + this.hikDevService.NET_DVR_GetLastError());
        }
        Integer longAlarmHandle = ConfigJsonUtil.getDeviceSearchInfoByIp(ip).getAlarmHandleId();
        if (null == longAlarmHandle || longAlarmHandle < 0) {
            //尚未布防,需要布防
            Pointer pUser = null;
            if (!this.hikDevService.NET_DVR_SetDVRMessageCallBack_V31(this, pUser)) {
                System.out.println("设置回调函数失败!");
            }

            NET_DVR_SETUPALARM_PARAM mStrAlarmInfo = new NET_DVR_SETUPALARM_PARAM();
            mStrAlarmInfo.dwSize = mStrAlarmInfo.size();
            //智能交通布防优先级：0- 一等级（高），1- 二等级（中），2- 三等级（低）
            mStrAlarmInfo.byLevel = 0;
            //智能交通报警信息上传类型：0- 老报警信息（NET_DVR_PLATE_RESULT），1- 新报警信息(NET_ITS_PLATE_RESULT)
            mStrAlarmInfo.byAlarmInfoType = 1;
            //布防类型(仅针对门禁主机、人证设备)：0-客户端布防(会断网续传)，1-实时布防(只上传实时数据)
            mStrAlarmInfo.byDeployType = 0;
            mStrAlarmInfo.write();
            Integer longUserId = ConfigJsonUtil.getDeviceSearchInfoByIp(ip).getLoginId();
            if (longUserId < 0) {
                return result.err("设备未注册");
            }
            longAlarmHandle = this.hikDevService.NET_DVR_SetupAlarmChan_V41(longUserId, mStrAlarmInfo);
            if (longAlarmHandle == -1) {
                // log.info("布防失败，错误号:{}", this.hikDevService.NET_DVR_GetLastError());
                result.err("布防失败，错误号:" + this.hikDevService.NET_DVR_GetLastError());
            } else {
                // log.info("布防成功");
                DeviceHandleDTO deviceHandleDTO = new DeviceHandleDTO();
                deviceHandleDTO.setAlarmHandleId(longAlarmHandle);
                deviceHandleDTO.setIpv4Address(ip);
                ConfigJsonUtil.updateDeviceHandle(deviceHandleDTO);
                result.ok("布防成功！");
            }
        } else {
            result.err("布防状态已存在，无需再次布防").data(longAlarmHandle);
        }
        return result;
    }

    @Override
    public HikDevResponse closeAlarmChan(String ip) {
        HikDevResponse result = new HikDevResponse();
        //报警撤防
        Integer longAlarmHandle = ConfigJsonUtil.getDeviceSearchInfoByIp(ip).getAlarmHandleId();
        if (null != longAlarmHandle && longAlarmHandle > -1) {
            if (this.hikDevService.NET_DVR_CloseAlarmChan_V30(longAlarmHandle)) {
                DeviceHandleDTO deviceHandleDTO = new DeviceHandleDTO();
                // 将状态id修改为-1
                deviceHandleDTO.setAlarmHandleId(-1);
                deviceHandleDTO.setIpv4Address(ip);
                ConfigJsonUtil.updateDeviceHandle(deviceHandleDTO);
                result.ok("撤防成功");
            } else {
                result.err("撤防失败");
            }
        } else {
            result.err("未布防，无须撤防");
        }
        return result;
    }

    /**
     * 回调函数
     *
     * @param lCommand
     * @param pAlarmer
     * @param pAlarmInfo
     * @param dwBufLen
     * @param pUser
     */
    private void alarmDataHandle(int lCommand, NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
        try {
            String[] newRow = new String[3];
            //报警时间
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String datetime = dateFormat.format(new Date());
            newRow[0] = datetime;
            //报警设备IP地址
            String deviceIp = new String(pAlarmer.sDeviceIP).trim();
            newRow[1] = deviceIp;
            //报警信息
            StringBuilder sAlarmType = new StringBuilder("lCommand=0x" + Integer.toHexString(lCommand));
            JSONObject alarmData = new JSONObject();
            alarmData.put("code", lCommand);
            alarmData.put("projectId", this.projectId);
            //lCommand是传的报警类型
//             log.info("报警信息主动上传V40：{}", lCommand);
            switch (lCommand) {
                case HikConstant.COMM_ALARM_V40:
                    NET_DVR_ALARMINFO_V40 struAlarmInfoV40 = new NET_DVR_ALARMINFO_V40();
                    struAlarmInfoV40.write();
                    Pointer pInfoV40 = struAlarmInfoV40.getPointer();
                    pInfoV40.write(0, pAlarmInfo.getByteArray(0, struAlarmInfoV40.size()), 0, struAlarmInfoV40.size());
                    struAlarmInfoV40.read();

                    switch (struAlarmInfoV40.struAlarmFixedHeader.dwAlarmType) {
                        case 0:
                            struAlarmInfoV40.struAlarmFixedHeader.ustruAlarm.setType(struIOAlarm.class);
                            struAlarmInfoV40.read();
                            sAlarmType.append("：信号量报警，报警输入口：").append(struAlarmInfoV40.struAlarmFixedHeader.ustruAlarm.struioAlarm.dwAlarmInputNo);
                            break;
                        case 1:
                            sAlarmType.append("：硬盘满");
                            break;
                        case 2:
                            sAlarmType.append("：信号丢失");
                            break;
                        case 3:
                            struAlarmInfoV40.struAlarmFixedHeader.ustruAlarm.setType(struAlarmChannel.class);
                            struAlarmInfoV40.read();
                            int iChanNum = struAlarmInfoV40.struAlarmFixedHeader.ustruAlarm.strualarmChannel.dwAlarmChanNum;
                            sAlarmType.append("：移动侦测，报警通道个数：").append(iChanNum).append("，报警通道号：");

                            for (int i = 0; i < iChanNum; i++) {
                                byte[] byChannel = struAlarmInfoV40.pAlarmData.getByteArray(i * 4L, 4);

                                int iChanneNo = 0;
                                for (int j = 0; j < 4; j++) {
                                    int ioffset = j * 8;
                                    int iByte = byChannel[j] & 0xff;
                                    iChanneNo = iChanneNo + (iByte << ioffset);
                                }

                                sAlarmType.append("+ch[").append(iChanneNo).append("]");
                            }
                            break;
                        case 4:
                            sAlarmType.append("：硬盘未格式化");
                            break;
                        case 5:
                            sAlarmType.append("：读写硬盘出错");
                            break;
                        case 6:
                            sAlarmType.append("：遮挡报警");
                            break;
                        case 7:
                            sAlarmType.append("：制式不匹配");
                            break;
                        case 8:
                            sAlarmType.append("：非法访问");
                            break;
                    }
                    //报警类型
                    newRow[2] = sAlarmType.toString();
                    // log.info("报警信息主动上传V40：{}", sAlarmType.toString());
                    break;
                case HikConstant.COMM_ALARM_V30:
                    NET_DVR_ALARMINFO_V30 strAlarmInfoV30 = new NET_DVR_ALARMINFO_V30();
                    strAlarmInfoV30.write();
                    Pointer pInfoV30 = strAlarmInfoV30.getPointer();
                    pInfoV30.write(0, pAlarmInfo.getByteArray(0, strAlarmInfoV30.size()), 0, strAlarmInfoV30.size());
                    strAlarmInfoV30.read();
                    switch (strAlarmInfoV30.dwAlarmType) {
                        case 0:
                            sAlarmType.append("：信号量报警，报警输入口：").append(strAlarmInfoV30.dwAlarmInputNumber + 1);
                            break;
                        case 1:
                            sAlarmType.append("：硬盘满");
                            break;
                        case 2:
                            sAlarmType.append("：信号丢失");
                            break;
                        case 3:
                            sAlarmType.append("：移动侦测，报警通道：");
                            StringBuilder alarmMsg = new StringBuilder();
                            alarmMsg.append("设备:").append(deviceIp).append("，发生移动侦测，报警通道：");
                            for (int i = 0; i < 64; i++) {
                                if (strAlarmInfoV30.byChannel[i] == 1) {
                                    sAlarmType.append("ch").append(i + 1);
                                    alarmMsg.append("ch").append(i + 1);
                                }
                            }
                            //==================写自己的业务代码===========================
                            JSONObject result = new JSONObject();
                            Map<String, Object> map = new HashMap<>(4);
                            map.put("deviceIp", deviceIp);
                            map.put("msg", alarmMsg);
                            map.put("datetime", datetime);
                            map.put("alarmMsg", "移动侦测");
                            result.put("code", 3);
                            result.put("data", map);
                            alarmData.putAll(result);
                            //==================写自己的业务代码===========================
                            break;
                        case 4:
                            sAlarmType.append("：硬盘未格式化");
                            break;
                        case 5:
                            sAlarmType.append("：读写硬盘出错");
                            break;
                        case 6:
                            sAlarmType.append("：遮挡报警");
                            break;
                        case 7:
                            sAlarmType.append("：制式不匹配");
                            break;
                        case 8:
                            sAlarmType.append("：非法访问");
                            break;
                    }
                    //报警类型
                    newRow[2] = sAlarmType.toString();
                    // log.info("报警信息主动上传V30：{}", sAlarmType);
                    break;
                case HikConstant.COMM_ALARM_RULE:
                    NET_VCA_RULE_ALARM strVcaAlarm = new NET_VCA_RULE_ALARM();
                    strVcaAlarm.write();
                    Pointer pVcaInfo = strVcaAlarm.getPointer();
                    pVcaInfo.write(0, pAlarmInfo.getByteArray(0, strVcaAlarm.size()), 0, strVcaAlarm.size());
                    strVcaAlarm.read();
                    String alarmInfo = "_wPort:" + strVcaAlarm.struDevInfo.wPort + "_byChannel:" + strVcaAlarm.struDevInfo.byChannel + "_byIvmsChannel:" + strVcaAlarm.struDevInfo.byIvmsChannel + "_Dev IP：" + new String(strVcaAlarm.struDevInfo.struDevIP.sIpV4);
                    switch (strVcaAlarm.struRuleInfo.wEventTypeEx) {
                        case 1:
                            sAlarmType.append("：穿越警戒面，").append(alarmInfo);
                            break;
                        case 2:
                            sAlarmType.append("：目标进入区域，").append(alarmInfo);
                            break;
                        case 3:
                            sAlarmType.append("：目标离开区域，").append(alarmInfo);
                            break;
                        default:
                            sAlarmType.append("：其他行为分析报警，事件类型：").append(strVcaAlarm.struRuleInfo.wEventTypeEx).append(alarmInfo);
                            break;
                    }
                    //报警类型
                    newRow[2] = sAlarmType.toString();

                    if (strVcaAlarm.dwPicDataLen > 0) {
                        String filename = this.fileStream.touchJpg();
                        this.fileStream.downloadToLocal(filename, strVcaAlarm.pImage.getByteArray(0, strVcaAlarm.dwPicDataLen));
                    }
                    // log.info("行为分析信息上传：{}", sAlarmType.toString());
                    break;
                case HikConstant.COMM_UPLOAD_PLATE_RESULT:
                    NET_DVR_PLATE_RESULT strPlateResult = new NET_DVR_PLATE_RESULT();
                    strPlateResult.write();
                    Pointer pPlateInfo = strPlateResult.getPointer();
                    pPlateInfo.write(0, pAlarmInfo.getByteArray(0, strPlateResult.size()), 0, strPlateResult.size());
                    strPlateResult.read();
                    String srt3 = new String(strPlateResult.struPlateInfo.sLicense, "GBK");
                    sAlarmType.append("：交通抓拍上传，车牌：").append(srt3);
                    //报警类型
                    newRow[2] = sAlarmType.toString();

                    if (strPlateResult.dwPicLen > 0) {
                        String filename = this.fileStream.touchJpg();
                        this.fileStream.downloadToLocal(filename, strPlateResult.pBuffer1.getByteArray(0, strPlateResult.dwPicLen));
                    }
                    // log.info("交通抓拍结果上传：{}", sAlarmType.toString());
                    break;
                case HikConstant.COMM_ITS_PLATE_RESULT:
                    NET_ITS_PLATE_RESULT strItsPlateResult = new NET_ITS_PLATE_RESULT();
                    strItsPlateResult.write();
                    Pointer pItsPlateInfo = strItsPlateResult.getPointer();
                    pItsPlateInfo.write(0, pAlarmInfo.getByteArray(0, strItsPlateResult.size()), 0, strItsPlateResult.size());
                    strItsPlateResult.read();
                    sAlarmType.append(",车辆类型：").append(strItsPlateResult.byVehicleType).append(",交通抓拍上传，车牌：").append(new String(strItsPlateResult.struPlateInfo.sLicense, "GBK"));
                    //报警类型
                    newRow[2] = sAlarmType.toString();
                    for (int i = 0; i < strItsPlateResult.dwPicNum; i++) {
                        if (strItsPlateResult.struPicInfo[i].dwDataLen > 0) {
                            String filename = this.fileStream.touchJpg();
                            this.fileStream.downloadToLocal(filename, strItsPlateResult.struPicInfo[i].pBuffer.getByteArray(0, strItsPlateResult.struPicInfo[i].dwDataLen));
                        }
                    }
                    // log.info("交通抓拍的终端图片上传：{}", sAlarmType.toString());
                    break;
                case HikConstant.COMM_ALARM_PDC:
                    NET_DVR_PDC_ALRAM_INFO strPDCResult = new NET_DVR_PDC_ALRAM_INFO();
                    strPDCResult.write();
                    Pointer pPDCInfo = strPDCResult.getPointer();
                    pPDCInfo.write(0, pAlarmInfo.getByteArray(0, strPDCResult.size()), 0, strPDCResult.size());
                    strPDCResult.read();
                    // byMode 0-单帧统计结果，1-最小时间段统计结果
                    if (strPDCResult.byMode == 0) {
                        strPDCResult.uStatModeParam.setType(NET_DVR_STATFRAME.class);
                        sAlarmType.append("：客流量统计，进入人数:").append(strPDCResult.dwEnterNum)
                                .append("，离开人数：").append(strPDCResult.dwLeaveNum)
                                .append(", byMode:").append(strPDCResult.byMode)
                                .append(", dwRelativeTime:").append(strPDCResult.uStatModeParam.struStatFrame.dwRelativeTime)
                                .append(", dwAbsTime:").append(strPDCResult.uStatModeParam.struStatFrame.dwAbsTime);
                        alarmData.put("countMode", strPDCResult.byMode);
                        alarmData.put("enterNum", strPDCResult.dwEnterNum);
                        alarmData.put("leaveNum", strPDCResult.dwLeaveNum);
                        alarmData.put("relativeTime", strPDCResult.uStatModeParam.struStatFrame.dwRelativeTime);
                        alarmData.put("absTime", strPDCResult.uStatModeParam.struStatFrame.dwAbsTime);
                    } else if (strPDCResult.byMode == 1) {
                        strPDCResult.uStatModeParam.setType(NET_DVR_STATTIME.class);
                        String startTime = strPDCResult.uStatModeParam.struStatTime.tmStart.toStringTimeDateFormat();
                        String endTime = strPDCResult.uStatModeParam.struStatTime.tmEnd.toStringTimeDateFormat();
                        sAlarmType.append(":客流量统计，进入人数:").append(strPDCResult.dwEnterNum)
                                .append(", 离开人数:").append(strPDCResult.dwLeaveNum)
                                .append(", byMode:").append(strPDCResult.byMode)
                                .append(", 开始时间:").append(startTime)
                                .append(", 结束时间 :").append(endTime);
                        alarmData.put("countMode", strPDCResult.byMode);
                        alarmData.put("enterNum", strPDCResult.dwEnterNum);
                        alarmData.put("leaveNum", strPDCResult.dwLeaveNum);
                        alarmData.put("startTime", startTime);
                        alarmData.put("endTime", endTime);
                    }
                    //报警类型
                    newRow[2] = sAlarmType.toString();
                    // log.info("客流量统计报警上传：{}", sAlarmType);
                    this.mqttConnectClient.publish(alarmData.toJSONString());
                    break;
                case HikConstant.COMM_ITS_PARK_VEHICLE:
                    NET_ITS_PARK_VEHICLE strItsParkVehicle = new NET_ITS_PARK_VEHICLE();
                    strItsParkVehicle.write();
                    Pointer pItsParkVehicle = strItsParkVehicle.getPointer();
                    pItsParkVehicle.write(0, pAlarmInfo.getByteArray(0, strItsParkVehicle.size()), 0, strItsParkVehicle.size());
                    strItsParkVehicle.read();
                    String srtParkingNo = new String(strItsParkVehicle.byParkingNo).trim(); //车位编号
                    String srtPlate = new String(strItsParkVehicle.struPlateInfo.sLicense, "GBK").trim(); //车牌号码
                    sAlarmType.append(",停产场数据,车位编号：").append(srtParkingNo).append(",车位状态：").append(strItsParkVehicle.byLocationStatus).append(",车牌：").append(srtPlate);
                    //报警类型
                    newRow[2] = sAlarmType.toString();
                    for (int i = 0; i < strItsParkVehicle.dwPicNum; i++) {
                        if (strItsParkVehicle.struPicInfo[i].dwDataLen > 0) {
                            String filename = this.fileStream.touchJpg();
                            this.fileStream.downloadToLocal(filename, strItsParkVehicle.struPicInfo[i].pBuffer.getByteArray(0, strItsParkVehicle.struPicInfo[i].dwDataLen));
                        }
                    }
                    // log.info("停车场数据：{}", sAlarmType.toString());
                    break;
                case HikConstant.COMM_ALARM_TFS:
                    NET_DVR_TFS_ALARM strTFSAlarmInfo = new NET_DVR_TFS_ALARM();
                    strTFSAlarmInfo.write();
                    Pointer pTFSInfo = strTFSAlarmInfo.getPointer();
                    pTFSInfo.write(0, pAlarmInfo.getByteArray(0, strTFSAlarmInfo.size()), 0, strTFSAlarmInfo.size());
                    strTFSAlarmInfo.read();
                    //车牌号码
                    sAlarmType.append("：交通取证报警信息，违章类型：").append(strTFSAlarmInfo.dwIllegalType).append("，车牌号码：").append(new String(strTFSAlarmInfo.struPlateInfo.sLicense, "GBK").trim()).append("，车辆出入状态：").append(strTFSAlarmInfo.struAIDInfo.byVehicleEnterState);
                    //报警类型
                    newRow[2] = sAlarmType.toString();
                    //报警设备IP地址
//                    String sIP = new String(strTFSAlarmInfo.struDevInfo.struDevIP.sIpV4).split("\0", 2);
                    // log.info("交通取证报警信息：{}", sAlarmType.toString());
                    break;
                case HikConstant.COMM_ALARM_AID_V41:
                    NET_DVR_AID_ALARM_V41 struAIDAlarmInfo = new NET_DVR_AID_ALARM_V41();
                    struAIDAlarmInfo.write();
                    Pointer pAIDInfo = struAIDAlarmInfo.getPointer();
                    pAIDInfo.write(0, pAlarmInfo.getByteArray(0, struAIDAlarmInfo.size()), 0, struAIDAlarmInfo.size());
                    struAIDAlarmInfo.read();
                    sAlarmType.append("：交通事件报警信息，交通事件类型：").append(struAIDAlarmInfo.struAIDInfo.dwAIDType).append("，规则ID：").append(struAIDAlarmInfo.struAIDInfo.byRuleID).append("，车辆出入状态：").append(struAIDAlarmInfo.struAIDInfo.byVehicleEnterState);
                    //报警类型
                    newRow[2] = sAlarmType.toString();
                    // log.info("交通事件报警信息扩展：{}", sAlarmType.toString());
                    break;
                case HikConstant.COMM_ALARM_TPS_V41:
                    NET_DVR_TPS_ALARM_V41 struTPSAlarmInfo = new NET_DVR_TPS_ALARM_V41();
                    struTPSAlarmInfo.write();
                    Pointer pTPSInfo = struTPSAlarmInfo.getPointer();
                    pTPSInfo.write(0, pAlarmInfo.getByteArray(0, struTPSAlarmInfo.size()), 0, struTPSAlarmInfo.size());
                    struTPSAlarmInfo.read();

                    sAlarmType.append("：交通统计报警信息，绝对时标：").append(struTPSAlarmInfo.dwAbsTime).append("，能见度:").append(struTPSAlarmInfo.struDevInfo.byIvmsChannel).append("，车道1交通状态:").append(struTPSAlarmInfo.struTPSInfo.struLaneParam[0].byTrafficState).append("，监测点编号：").append(new String(struTPSAlarmInfo.byMonitoringSiteID).trim()).append("，设备编号：").append(new String(struTPSAlarmInfo.byDeviceID).trim()).append("，开始统计时间：").append(struTPSAlarmInfo.dwStartTime).append("，结束统计时间：").append(struTPSAlarmInfo.dwStopTime);
                    //报警类型
                    newRow[2] = sAlarmType.toString();
                    // log.info("交通事件报警信息扩展：{}", sAlarmType.toString());
                    break;
                case HikConstant.COMM_UPLOAD_FACESNAP_RESULT:
                    //实时人脸抓拍上传
                    NET_VCA_FACESNAP_RESULT strFaceSnapInfo = new NET_VCA_FACESNAP_RESULT();
                    strFaceSnapInfo.write();
                    Pointer pFaceSnapInfo = strFaceSnapInfo.getPointer();
                    pFaceSnapInfo.write(0, pAlarmInfo.getByteArray(0, strFaceSnapInfo.size()), 0, strFaceSnapInfo.size());
                    strFaceSnapInfo.read();
                    sAlarmType.append("：人脸抓拍上传，人脸评分：").append(strFaceSnapInfo.dwFaceScore)
                            .append("，年龄段：").append(strFaceSnapInfo.struFeature.byAgeGroup)
                            .append("，性别：").append(strFaceSnapInfo.struFeature.bySex);
                    alarmData.put("faceScore", strFaceSnapInfo.dwFaceScore);
                    alarmData.put("ageGroup", strFaceSnapInfo.struFeature.byAgeGroup);
                    alarmData.put("sex", strFaceSnapInfo.struFeature.bySex);
                    //报警类型
                    newRow[2] = sAlarmType.toString();
                    if (strFaceSnapInfo.dwFacePicLen > 0) {
                        //人脸图片写文件 小图 人脸图
                        String touchJpg = this.fileStream.touchJpg();
                        this.fileStream.downloadToLocal(touchJpg, strFaceSnapInfo.pBuffer1.getByteArray(0, strFaceSnapInfo.dwFacePicLen));
                        String encode = Base64.encode(new File(touchJpg));
                        alarmData.put("smallFacePic", encode);
                    }
                    if (strFaceSnapInfo.dwBackgroundPicLen > 0) {
                        //人脸图片写文件 大图 背景图
                        String touchJpg = this.fileStream.touchJpg();
                        this.fileStream.downloadToLocal(touchJpg, strFaceSnapInfo.pBuffer2.getByteArray(0, strFaceSnapInfo.dwBackgroundPicLen));
                        String encode = Base64.encode(new File(touchJpg));
                        alarmData.put("bigFacePic", encode);
                    }
                     log.info("人脸识别结果：{}", sAlarmType.toString());
                    break;
                case HikConstant.COMM_SNAP_MATCH_ALARM:
                    //人脸名单比对报警
                    NET_VCA_FACESNAP_MATCH_ALARM strFaceSnapMatch = new NET_VCA_FACESNAP_MATCH_ALARM();
                    strFaceSnapMatch.write();
                    Pointer pFaceSnapMatch = strFaceSnapMatch.getPointer();
                    pFaceSnapMatch.write(0, pAlarmInfo.getByteArray(0, strFaceSnapMatch.size()), 0, strFaceSnapMatch.size());
                    strFaceSnapMatch.read();

                    if ((strFaceSnapMatch.dwSnapPicLen > 0) && (strFaceSnapMatch.byPicTransType == 0)) {
                        String filename = this.fileStream.touchJpg();
                        this.fileStream.downloadToLocal(filename, strFaceSnapMatch.pSnapPicBuffer.getByteArray(0, strFaceSnapMatch.dwSnapPicLen));
                        // TODO 以后测试一下能不能直接使用strFaceSnapMatch.pSnapPicBuffer.getByteArray(0, strFaceSnapMatch.dwSnapPicLen)
                        String encode = Base64.encode(new File(filename));
                        alarmData.put("bigFacePic", encode);
                    }
                    if ((strFaceSnapMatch.struSnapInfo.dwSnapFacePicLen > 0) && (strFaceSnapMatch.byPicTransType == 0)) {
                        String filename = this.fileStream.touchJpg();
                        //将字节写入文件
                        this.fileStream.downloadToLocal(filename, strFaceSnapMatch.struSnapInfo.pBuffer1.getByteArray(0, strFaceSnapMatch.struSnapInfo.dwSnapFacePicLen));
                        String encode = Base64.encode(new File(filename));
                        alarmData.put("smallFacePic", encode);
                    }
                    // 禁止名单人脸子图
                    if ((strFaceSnapMatch.struBlockListInfo.dwBlockListPicLen > 0) && (strFaceSnapMatch.byPicTransType == 0)) {
                        String filename = this.fileStream.touchJpg();
                        this.fileStream.downloadToLocal(filename, strFaceSnapMatch.struBlockListInfo.pBuffer1.getByteArray(0, strFaceSnapMatch.struBlockListInfo.dwBlockListPicLen));
                        String encode = Base64.encode(new File(filename));
                        alarmData.put("blackSmallFacePic", encode);
                    }
                    String name = new String(strFaceSnapMatch.struBlockListInfo.struBlockListInfo.struAttribute.byName, "GBK").trim();
                    String certificateNumber = new String(strFaceSnapMatch.struBlockListInfo.struBlockListInfo.struAttribute.byCertificateNumber).trim();
                    sAlarmType.append("：人脸名单比对报警，相识度：").append(strFaceSnapMatch.fSimilarity)
                            .append("，名单姓名：").append(name)
                            .append("，\n名单证件信息：").append(certificateNumber);
                    alarmData.put("faceScore", strFaceSnapMatch.fSimilarity);
                    alarmData.put("name", name);
                    alarmData.put("certificateNumber", certificateNumber);

                    //获取人脸库ID
                    byte[] FDIDbytes;
                    if ((strFaceSnapMatch.struBlockListInfo.dwFDIDLen > 0) && (strFaceSnapMatch.struBlockListInfo.pFDID != null)) {
                        ByteBuffer FDIDbuffers = strFaceSnapMatch.struBlockListInfo.pFDID.getByteBuffer(0, strFaceSnapMatch.struBlockListInfo.dwFDIDLen);
                        FDIDbytes = new byte[strFaceSnapMatch.struBlockListInfo.dwFDIDLen];
                        FDIDbuffers.rewind();
                        FDIDbuffers.get(FDIDbytes);
                        sAlarmType.append("，人脸库ID:").append(new String(FDIDbytes).trim());
                        alarmData.put("faceDataId", new String(FDIDbytes).trim());
                    }
                    //获取人脸图片ID
                    byte[] PIDbytes;
                    if ((strFaceSnapMatch.struBlockListInfo.dwPIDLen > 0) && (strFaceSnapMatch.struBlockListInfo.pPID != null)) {
                        ByteBuffer PIDbuffers = strFaceSnapMatch.struBlockListInfo.pPID.getByteBuffer(0, strFaceSnapMatch.struBlockListInfo.dwPIDLen);
                        PIDbytes = new byte[strFaceSnapMatch.struBlockListInfo.dwPIDLen];
                        PIDbuffers.rewind();
                        PIDbuffers.get(PIDbytes);
                        sAlarmType.append("，人脸图片ID:").append(new String(PIDbytes).trim());
                        alarmData.put("facePicId", new String(PIDbytes).trim());
                    }
                    //报警类型
                    newRow[2] = sAlarmType.toString();
                    // log.info("人脸比对结果上传：{}", sAlarmType.toString());
                    break;
                case HikConstant.COMM_ALARM_ACS:
                    //门禁主机报警信息
                    NET_DVR_ACS_ALARM_INFO strACSInfo = new NET_DVR_ACS_ALARM_INFO();
                    strACSInfo.write();
                    Pointer pACSInfo = strACSInfo.getPointer();
                    pACSInfo.write(0, pAlarmInfo.getByteArray(0, strACSInfo.size()), 0, strACSInfo.size());
                    strACSInfo.read();
                    String eventDatetime = strACSInfo.struTime.toStringTimeDateFormat();
                    if (strACSInfo.dwPicDataLen > 0) {
                        // 抓取到照片
                        JSONObject data = this.personInfo(strACSInfo, pAlarmer);
                        // minorAlarmType报警次类型dwMinor 参考宏定义{1024:防区短路报警,21:门锁打开,22:门锁关闭}
                        String pathname = this.fileStream.touchJpg();
                        byte[] picDataByteArray = strACSInfo.pPicData.getByteArray(0, strACSInfo.dwPicDataLen);
                        this.fileStream.downloadToLocal(pathname, picDataByteArray);
                        // log.info("新设备抓取实时照片事件:{} 发生时间：{}", pathname, eventDatetime);
                        String upload = Base64.encode(picDataByteArray);
                        data.put("pic", upload);
                        alarmData.putAll(data);
                        //TODO 上报布防事件
//                        ThreadUtil.execAsync(() -> HttpUtil.post("http://localhost:4068/hik/api/accessControlEvent", JSONObject.toJSONString(mqttMsg)));

                    } else {
                        JSONObject data = this.personInfo(strACSInfo, pAlarmer);
                        //获取到卡信息
                        String cardNo = new String(strACSInfo.struAcsEventInfo.byCardNo).trim();
                        if (StrUtil.isNotBlank(cardNo)) {
                            String pathname = this.hikCardService.selectFaceByCardNo(cardNo, deviceIp);
                            // log.info("旧设备读取人脸照片:{},发生时间:{}", pathname, eventDatetime);
                            String personName = this.hikCardService.selectPersonByCardNo(cardNo, deviceIp);
                            if (StrUtil.isNotBlank(personName) && StrUtil.isNotBlank(pathname)) {
                                // log.info("the employeeNo:{}", personName);
                                data.put("employeeNo", personName);
                                String upload = Base64.encode(new File(pathname));
                                data.put("pic", upload);
                               }
                        }
                        alarmData.putAll(data);
                        //TODO 上报布防事件
                        //ThreadUtil.execAsync(() -> HttpUtil.post("http://localhost:4068/hik/api/accessControlEvent", JSONObject.toJSONString(mqttMsg)));

                    }
                    break;
                case HikConstant.COMM_ID_INFO_ALARM: //身份证信息
                    NET_DVR_ID_CARD_INFO_ALARM strIDCardInfo = new NET_DVR_ID_CARD_INFO_ALARM();
                    strIDCardInfo.write();
                    Pointer pIDCardInfo = strIDCardInfo.getPointer();
                    pIDCardInfo.write(0, pAlarmInfo.getByteArray(0, strIDCardInfo.size()), 0, strIDCardInfo.size());
                    strIDCardInfo.read();

                    sAlarmType.append("：门禁身份证刷卡信息，身份证号码：").append(new String(strIDCardInfo.struIDCardCfg.byIDNum).trim())
                            .append("，姓名：").append(new String(strIDCardInfo.struIDCardCfg.byName).trim())
                            .append("，报警主类型：").append(strIDCardInfo.dwMajor).append("，报警次类型：").append(strIDCardInfo.dwMinor);
                    //报警类型
                    newRow[2] = sAlarmType.toString();
                    //身份证图片
                    if (strIDCardInfo.dwPicDataLen > 0) {
                        String filename = this.fileStream.touchJpg();
                        this.fileStream.downloadToLocal(filename, strIDCardInfo.pPicData.getByteArray(0, strIDCardInfo.dwPicDataLen));
                    }

                    //抓拍图片
                    if (strIDCardInfo.dwCapturePicDataLen > 0) {
                        String filename = this.fileStream.touchJpg();
                        this.fileStream.downloadToLocal(filename, strIDCardInfo.pCapturePicData.getByteArray(0, strIDCardInfo.dwCapturePicDataLen));
                    }
                    // log.info("门禁身份证刷卡信息：{}", sAlarmType.toString());
                    break;
                case HikConstant.COMM_UPLOAD_AIOP_VIDEO: //设备支持AI开放平台接入，上传视频检测数据
                    NET_AIOP_VIDEO_HEAD struAIOPVideo = new NET_AIOP_VIDEO_HEAD();
                    struAIOPVideo.write();
                    Pointer pAIOPVideo = struAIOPVideo.getPointer();
                    pAIOPVideo.write(0, pAlarmInfo.getByteArray(0, struAIOPVideo.size()), 0, struAIOPVideo.size());
                    struAIOPVideo.read();

                    String eventTime = struAIOPVideo.struTime.toStringTimeDateFormat();
                    sAlarmType.append("：AI开放平台接入，上传视频检测数据，通道号:").append(struAIOPVideo.dwChannel).append(", 时间:").append(eventTime);
                    //报警类型
                    newRow[2] = sAlarmType.toString();
                    // 解析json数据
                    if (struAIOPVideo.dwAIOPDataSize > 0) {
                        JSONObject jsonObject = JSON.parseObject(struAIOPVideo.pBufferAIOPData.getByteArray(0, struAIOPVideo.dwAIOPDataSize));
                        JSONArray jsonArray = jsonObject.getJSONObject("events").getJSONArray("alertInfo");
                        List<Integer> ruleIdList = new ArrayList<>();
                        for (int i=0;i < jsonArray.size(); i++) {
                            Integer ruleId = jsonArray.getJSONObject(i).getJSONObject("ruleInfo").getInteger("ruleID");
                            // TODO 这些ruleId是从摄像头那端自行设置的规则id，1：玩手机；2：睡岗；3：离岗
                            ruleIdList.add(ruleId);
                        }
                        alarmData.put("ruleIdList", ruleIdList);
                    }
                    if (struAIOPVideo.dwPictureSize > 0) {
                        String filename = this.fileStream.touchJpg();
                        this.fileStream.downloadToLocal(filename, struAIOPVideo.pBufferPicture.getByteArray(0, struAIOPVideo.dwPictureSize));
                        String encode = Base64.encode(new File(filename));
                        alarmData.put("facePic", encode);
                    }
                    // log.info("设备支持AI开放平台接入，上传视频检测数据：{}", sAlarmType.toString());
                    break;
                case HikConstant.COMM_UPLOAD_AIOP_PICTURE: //设备支持AI开放平台接入，上传视频检测数据
                    NET_AIOP_PICTURE_HEAD struAIOPPic = new NET_AIOP_PICTURE_HEAD();
                    struAIOPPic.write();
                    Pointer pAIOPPic = struAIOPPic.getPointer();
                    pAIOPPic.write(0, pAlarmInfo.getByteArray(0, struAIOPPic.size()), 0, struAIOPPic.size());
                    struAIOPPic.read();

                    String strPicTime = struAIOPPic.struTime.toStringTimeDateFormat();

                    sAlarmType.append("：AI开放平台接入，上传图片检测数据，通道号:").append(new String(struAIOPPic.szPID)).append(", 时间:").append(strPicTime);
                    //报警类型
                    newRow[2] = sAlarmType.toString();
                    if (struAIOPPic.dwAIOPDataSize > 0) {
                        String filename = this.fileStream.touchJpg();
                        this.fileStream.downloadToLocal(filename, struAIOPPic.pBufferAIOPData.getByteArray(0, struAIOPPic.dwAIOPDataSize));
                    }
                    // log.info("设备支持AI开放平台接入，上传图片检测数据：{}", sAlarmType.toString());
                    break;
                case HikConstant.COMM_ISAPI_ALARM: //ISAPI协议报警信息
                    NET_DVR_ALARM_ISAPI_INFO struEventISAPI = new NET_DVR_ALARM_ISAPI_INFO();
                    struEventISAPI.write();
                    Pointer pEventISAPI = struEventISAPI.getPointer();
                    pEventISAPI.write(0, pAlarmInfo.getByteArray(0, struEventISAPI.size()), 0, struEventISAPI.size());
                    struEventISAPI.read();

                    sAlarmType.append("：ISAPI协议报警信息, 数据格式:").append(struEventISAPI.byDataType)
                            .append(", 图片个数:").append(struEventISAPI.byPicturesNumber);
                    //报警类型
                    newRow[2] = sAlarmType.toString();
                    if (struEventISAPI.dwAlarmDataLen > 0) {
                        String filename = this.fileStream.touchJpg();
                        this.fileStream.downloadToLocal(filename, struEventISAPI.pAlarmData.getByteArray(0, struEventISAPI.dwAlarmDataLen));
                    }

                    for (int i = 0; i < struEventISAPI.byPicturesNumber; i++) {
                        NET_DVR_ALARM_ISAPI_PICDATA struPicData = new NET_DVR_ALARM_ISAPI_PICDATA();
                        struPicData.write();
                        Pointer pPicData = struPicData.getPointer();
                        pPicData.write(0, struEventISAPI.pPicPackData.getByteArray((long) i * struPicData.size(), struPicData.size()), 0, struPicData.size());
                        struPicData.read();

                        if (struPicData.dwPicLen > 0) {
                            String filename = this.fileStream.touchJpg();
                            this.fileStream.downloadToLocal(filename, struPicData.pPicData.getByteArray(0, struPicData.dwPicLen));
                        }
                    }
                    // log.info("ISAPI协议报警信息：{}", sAlarmType);
                    alarmData.put("msg", sAlarmType);
                    break;
                default:
                    //报警类型
                    newRow[2] = sAlarmType.toString();
                    // log.info("其他信息：{},lCommand是传的报警类型:{}", sAlarmType, lCommand);
                    break;
            }
            alarmData.put("deviceIp", deviceIp);
            alarmData.put("msg", sAlarmType);
            this.mqttConnectClient.publish(alarmData.toJSONString());
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
    }

    public JSONObject personInfo(NET_DVR_ACS_ALARM_INFO strACSInfo, NET_DVR_ALARMER pAlarmer) {
        JSONObject data = new JSONObject();
        String cardNo = new String(strACSInfo.struAcsEventInfo.byCardNo).trim();
        int employeeNo = strACSInfo.struAcsEventInfo.dwEmployeeNo;

        String deviceIp = new String(pAlarmer.sDeviceIP).trim();
        String deviceName = new String(pAlarmer.sDeviceName).trim();
        data.put("cardNo", cardNo);
        data.put("employeeNo", employeeNo);
        data.put("deviceIp", deviceIp);
        data.put("deviceName", deviceName);
        NET_DVR_TIME struTime = strACSInfo.struTime;
        String eventTime = struTime.dwYear + "-" + struTime.dwMonth + "-" + struTime.dwDay + " " + struTime.dwHour + ":" + struTime.dwMinute + ":" + struTime.dwSecond;
        data.put("eventTime", eventTime);
        data.put("majorAlarmType", strACSInfo.dwMajor);
        data.put("minorAlarmType", strACSInfo.dwMinor);
        data.put("cardType", strACSInfo.struAcsEventInfo.byCardType);
        // log.info("门禁主机报警信息=====>{}", data);
        return data;
    }
}
