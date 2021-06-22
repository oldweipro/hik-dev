package com.oldwei.hikdev.sdk.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.oldwei.hikdev.sdk.constant.HikConstant;
import com.oldwei.hikdev.sdk.constant.RedisPrefixConstant;
import com.oldwei.hikdev.sdk.service.FMSGCallBack_V31;
import com.oldwei.hikdev.sdk.service.IHikAlarmDataService;
import com.oldwei.hikdev.sdk.service.IHikDevService;
import com.oldwei.hikdev.sdk.structure.*;
import com.oldwei.hikdev.utils.CloudUploadUtil;
import com.sun.jna.Pointer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author oldwei
 * @date 2021-5-18 12:05
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HikAlarmDataServiceImpl implements IHikAlarmDataService, FMSGCallBack_V31 {
    private final IHikDevService hikDevService;

    private final RedisTemplate<String, Serializable> redisTemplate;

    @Override
    public boolean invoke(int lCommand, NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
        this.alarmDataHandle(lCommand, pAlarmer, pAlarmInfo, dwBufLen, pUser);
        return true;
    }

    @Override
    public JSONObject setupAlarmChan(JSONObject jsonObject) {
        JSONObject result = new JSONObject();
        result.put("event", jsonObject.getString("event"));
        String ip = jsonObject.getString("ip");
        NET_DVR_LOCAL_GENERAL_CFG struGeneralCfg = new NET_DVR_LOCAL_GENERAL_CFG();
        // 控制JSON透传报警数据和图片是否分离，0-不分离，1-分离（分离后走COMM_ISAPI_ALARM回调返回）
        struGeneralCfg.byAlarmJsonPictureSeparate = 1;
        struGeneralCfg.write();

        if (!this.hikDevService.NET_DVR_SetSDKLocalCfg(17, struGeneralCfg.getPointer())) {
            log.info("NET_DVR_SetSDKLocalCfg失败");
            result.put("code", -1);
            result.put("msg", "NET_DVR_StopRemoteConfig接口调用失败，错误码：" + this.hikDevService.NET_DVR_GetLastError());
            return result;
        }
        Integer longAlarmHandle = (Integer) this.redisTemplate.opsForValue().get(RedisPrefixConstant.HIK_ALARM_HANDLE_IP + ip);
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
            Integer longUserId = (Integer) this.redisTemplate.opsForValue().get(RedisPrefixConstant.HIK_REG_USERID_IP + ip);
            if (null == longUserId) {
                result.put("code", -1);
                result.put("msg", "设备状态未注册！");
                return result;
            }
            longAlarmHandle = this.hikDevService.NET_DVR_SetupAlarmChan_V41(longUserId, mStrAlarmInfo);
            if (longAlarmHandle == -1) {
                log.info("布防失败，错误号:{}", this.hikDevService.NET_DVR_GetLastError());
                result.put("code", -1);
                result.put("msg", "布防失败，错误号:" + this.hikDevService.NET_DVR_GetLastError());
            } else {
                log.info("布防成功");
                this.redisTemplate.opsForValue().set(RedisPrefixConstant.HIK_ALARM_HANDLE_IP + ip, longAlarmHandle);
                result.put("code", 0);
                result.put("msg", "布防成功！");
            }
        }
        return result;
    }

    @Override
    public JSONObject closeAlarmChan(JSONObject jsonObject) {
        JSONObject result = new JSONObject();
        String ip = jsonObject.getString("ip");
        //报警撤防
        Integer longAlarmHandle = (Integer) this.redisTemplate.opsForValue().get(RedisPrefixConstant.HIK_ALARM_HANDLE_IP + ip);
        if (null != longAlarmHandle && longAlarmHandle > -1) {
            if (this.hikDevService.NET_DVR_CloseAlarmChan_V30(longAlarmHandle)) {
                longAlarmHandle = -1;
                this.redisTemplate.opsForValue().set(RedisPrefixConstant.HIK_ALARM_HANDLE_IP + ip, longAlarmHandle);
                log.info("撤防成功");
                result.put("code", 0);
                result.put("msg", "撤防成功");
            } else {
                log.info("撤防成功");
                result.put("code", -1);
                result.put("msg", "撤防失败");
            }
        }
        return result;
    }

    @Override
    public JSONObject startAlarmListen(JSONObject jsonObject) {
        JSONObject result = new JSONObject();
        String ip = jsonObject.getString("ip");
        Short port = jsonObject.getShort("port");
        //报警撤防
        // TODO something... fMSFCallBack = new FMSGCallBack(); let me try FMSGCallBack_V31
        Pointer pUser = null;
        int startListenV30 = this.hikDevService.NET_DVR_StartListen_V30(ip, port, null, pUser);
        this.redisTemplate.opsForValue().set(RedisPrefixConstant.HIK_ALARM_LISTEN_IP + ip, startListenV30);
        if (startListenV30 < 0) {
            log.info("启动监听失败，错误号:{}", this.hikDevService.NET_DVR_GetLastError());
            result.put("code", -1);
            result.put("msg", "启动监听失败");
        } else {
            result.put("code", 0);
            result.put("msg", "启动监听成功");
            log.info("启动监听成功");
        }
        return result;
    }

    @Override
    public JSONObject stopAlarmListen(JSONObject jsonObject) {
        JSONObject result = new JSONObject();
        String ip = jsonObject.getString("ip");
        Integer startListenV30 = (Integer) this.redisTemplate.opsForValue().get(RedisPrefixConstant.HIK_ALARM_LISTEN_IP + ip);
        if (null == startListenV30 || startListenV30 < 0) {
            result.put("code", 0);
            result.put("msg", "停止监听成功");
            return result;
        }
        if (!this.hikDevService.NET_DVR_StopListen_V30(startListenV30)) {
            log.info("停止监听失败");
            result.put("code", -1);
            result.put("msg", "停止监听失败");
        } else {
            log.info("停止监听成功");
            result.put("code", 0);
            result.put("msg", "停止监听成功");
        }
        return result;
    }

    @Override
    public void alarmDataHandle(int lCommand, NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
        try {
            String sAlarmType = new String();
//            DefaultTableModel alarmTableModel = ((DefaultTableModel) jTableAlarm.getModel());//获取表格模型
            String[] newRow = new String[3];
            //报警时间
            Date today = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String[] sIP = new String[2];

            sAlarmType = new String("lCommand=0x") + Integer.toHexString(lCommand);

            //lCommand是传的报警类型
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
                            sAlarmType = sAlarmType + new String("：信号量报警") + "，" + "报警输入口：" + struAlarmInfoV40.struAlarmFixedHeader.ustruAlarm.struioAlarm.dwAlarmInputNo;
                            break;
                        case 1:
                            sAlarmType = sAlarmType + new String("：硬盘满");
                            break;
                        case 2:
                            sAlarmType = sAlarmType + new String("：信号丢失");
                            break;
                        case 3:
                            struAlarmInfoV40.struAlarmFixedHeader.ustruAlarm.setType(struAlarmChannel.class);
                            struAlarmInfoV40.read();
                            int iChanNum = struAlarmInfoV40.struAlarmFixedHeader.ustruAlarm.strualarmChannel.dwAlarmChanNum;
                            sAlarmType = sAlarmType + new String("：移动侦测") + "，" + "报警通道个数：" + iChanNum + "，" + "报警通道号：";

                            for (int i = 0; i < iChanNum; i++) {
                                byte[] byChannel = struAlarmInfoV40.pAlarmData.getByteArray(i * 4, 4);

                                int iChanneNo = 0;
                                for (int j = 0; j < 4; j++) {
                                    int ioffset = j * 8;
                                    int iByte = byChannel[j] & 0xff;
                                    iChanneNo = iChanneNo + (iByte << ioffset);
                                }

                                sAlarmType = sAlarmType + "+ch[" + iChanneNo + "]";
                            }

                            break;
                        case 4:
                            sAlarmType = sAlarmType + new String("：硬盘未格式化");
                            break;
                        case 5:
                            sAlarmType = sAlarmType + new String("：读写硬盘出错");
                            break;
                        case 6:
                            sAlarmType = sAlarmType + new String("：遮挡报警");
                            break;
                        case 7:
                            sAlarmType = sAlarmType + new String("：制式不匹配");
                            break;
                        case 8:
                            sAlarmType = sAlarmType + new String("：非法访问");
                            break;
                    }

                    newRow[0] = dateFormat.format(today);
                    //报警类型
                    newRow[1] = sAlarmType;
                    //报警设备IP地址
                    sIP = new String(pAlarmer.sDeviceIP).split("\0", 2);
                    newRow[2] = sIP[0];
                    log.info("报警信息主动上传V40：{}", sAlarmType);
                    break;
                case HikConstant.COMM_ALARM_V30:
                    NET_DVR_ALARMINFO_V30 strAlarmInfoV30 = new NET_DVR_ALARMINFO_V30();
                    strAlarmInfoV30.write();
                    Pointer pInfoV30 = strAlarmInfoV30.getPointer();
                    pInfoV30.write(0, pAlarmInfo.getByteArray(0, strAlarmInfoV30.size()), 0, strAlarmInfoV30.size());
                    strAlarmInfoV30.read();
                    switch (strAlarmInfoV30.dwAlarmType) {
                        case 0:
                            sAlarmType = sAlarmType + new String("：信号量报警") + "，" + "报警输入口：" + (strAlarmInfoV30.dwAlarmInputNumber + 1);
                            break;
                        case 1:
                            sAlarmType = sAlarmType + new String("：硬盘满");
                            break;
                        case 2:
                            sAlarmType = sAlarmType + new String("：信号丢失");
                            break;
                        case 3:
                            sAlarmType = sAlarmType + new String("：移动侦测") + "，" + "报警通道：";
                            for (int i = 0; i < 64; i++) {
                                if (strAlarmInfoV30.byChannel[i] == 1) {
                                    sAlarmType = sAlarmType + "ch" + (i + 1) + " ";
                                }
                            }
                            break;
                        case 4:
                            sAlarmType = sAlarmType + new String("：硬盘未格式化");
                            break;
                        case 5:
                            sAlarmType = sAlarmType + new String("：读写硬盘出错");
                            break;
                        case 6:
                            sAlarmType = sAlarmType + new String("：遮挡报警");
                            break;
                        case 7:
                            sAlarmType = sAlarmType + new String("：制式不匹配");
                            break;
                        case 8:
                            sAlarmType = sAlarmType + new String("：非法访问");
                            break;
                    }
                    newRow[0] = dateFormat.format(today);
                    //报警类型
                    newRow[1] = sAlarmType;
                    //报警设备IP地址
                    sIP = new String(pAlarmer.sDeviceIP).split("\0", 2);
                    newRow[2] = sIP[0];
                    log.info("报警信息主动上传V30：{}", sAlarmType);
                    break;
                case HikConstant.COMM_ALARM_RULE:
                    NET_VCA_RULE_ALARM strVcaAlarm = new NET_VCA_RULE_ALARM();
                    strVcaAlarm.write();
                    Pointer pVcaInfo = strVcaAlarm.getPointer();
                    pVcaInfo.write(0, pAlarmInfo.getByteArray(0, strVcaAlarm.size()), 0, strVcaAlarm.size());
                    strVcaAlarm.read();

                    switch (strVcaAlarm.struRuleInfo.wEventTypeEx) {
                        case 1:
                            sAlarmType = sAlarmType + new String("：穿越警戒面") + "，" +
                                    "_wPort:" + strVcaAlarm.struDevInfo.wPort +
                                    "_byChannel:" + strVcaAlarm.struDevInfo.byChannel +
                                    "_byIvmsChannel:" + strVcaAlarm.struDevInfo.byIvmsChannel +
                                    "_Dev IP：" + new String(strVcaAlarm.struDevInfo.struDevIP.sIpV4);
                            break;
                        case 2:
                            sAlarmType = sAlarmType + new String("：目标进入区域") + "，" +
                                    "_wPort:" + strVcaAlarm.struDevInfo.wPort +
                                    "_byChannel:" + strVcaAlarm.struDevInfo.byChannel +
                                    "_byIvmsChannel:" + strVcaAlarm.struDevInfo.byIvmsChannel +
                                    "_Dev IP：" + new String(strVcaAlarm.struDevInfo.struDevIP.sIpV4);
                            break;
                        case 3:
                            sAlarmType = sAlarmType + new String("：目标离开区域") + "，" +
                                    "_wPort:" + strVcaAlarm.struDevInfo.wPort +
                                    "_byChannel:" + strVcaAlarm.struDevInfo.byChannel +
                                    "_byIvmsChannel:" + strVcaAlarm.struDevInfo.byIvmsChannel +
                                    "_Dev IP：" + new String(strVcaAlarm.struDevInfo.struDevIP.sIpV4);
                            break;
                        default:
                            sAlarmType = sAlarmType + new String("：其他行为分析报警，事件类型：")
                                    + strVcaAlarm.struRuleInfo.wEventTypeEx +
                                    "_wPort:" + strVcaAlarm.struDevInfo.wPort +
                                    "_byChannel:" + strVcaAlarm.struDevInfo.byChannel +
                                    "_byIvmsChannel:" + strVcaAlarm.struDevInfo.byIvmsChannel +
                                    "_Dev IP：" + new String(strVcaAlarm.struDevInfo.struDevIP.sIpV4);
                            break;
                    }
                    newRow[0] = dateFormat.format(today);
                    //报警类型
                    newRow[1] = sAlarmType;
                    //报警设备IP地址
                    sIP = new String(pAlarmer.sDeviceIP).split("\0", 2);
                    newRow[2] = sIP[0];

                    if (strVcaAlarm.dwPicDataLen > 0) {
                        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                        String newName = sf.format(new Date());
                        FileOutputStream fout;
                        try {
                            fout = new FileOutputStream(".\\pic\\" + new String(pAlarmer.sDeviceIP).trim()
                                    + "wEventTypeEx[" + strVcaAlarm.struRuleInfo.wEventTypeEx + "]_" + newName + "_vca.jpg");
                            //将字节写入文件
                            long offset = 0;
                            ByteBuffer buffers = strVcaAlarm.pImage.getByteBuffer(offset, strVcaAlarm.dwPicDataLen);
                            byte[] bytes = new byte[strVcaAlarm.dwPicDataLen];
                            buffers.rewind();
                            buffers.get(bytes);
                            fout.write(bytes);
                            fout.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    log.info("行为分析信息上传：{}", sAlarmType);
                    break;
                case HikConstant.COMM_UPLOAD_PLATE_RESULT:
                    NET_DVR_PLATE_RESULT strPlateResult = new NET_DVR_PLATE_RESULT();
                    strPlateResult.write();
                    Pointer pPlateInfo = strPlateResult.getPointer();
                    pPlateInfo.write(0, pAlarmInfo.getByteArray(0, strPlateResult.size()), 0, strPlateResult.size());
                    strPlateResult.read();
                    try {
                        String srt3 = new String(strPlateResult.struPlateInfo.sLicense, "GBK");
                        sAlarmType = sAlarmType + "：交通抓拍上传，车牌：" + srt3;
                    } catch (UnsupportedEncodingException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    newRow[0] = dateFormat.format(today);
                    //报警类型
                    newRow[1] = sAlarmType;
                    //报警设备IP地址
                    sIP = new String(pAlarmer.sDeviceIP).split("\0", 2);
                    newRow[2] = sIP[0];

                    if (strPlateResult.dwPicLen > 0) {
                        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                        String newName = sf.format(new Date());
                        FileOutputStream fout;
                        try {
                            fout = new FileOutputStream(".\\pic\\" + new String(pAlarmer.sDeviceIP).trim() + "_"
                                    + newName + "_plateResult.jpg");
                            //将字节写入文件
                            long offset = 0;
                            ByteBuffer buffers = strPlateResult.pBuffer1.getByteBuffer(offset, strPlateResult.dwPicLen);
                            byte[] bytes = new byte[strPlateResult.dwPicLen];
                            buffers.rewind();
                            buffers.get(bytes);
                            fout.write(bytes);
                            fout.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    log.info("交通抓拍结果上传：{}", sAlarmType);
                    break;
                case HikConstant.COMM_ITS_PLATE_RESULT:
                    NET_ITS_PLATE_RESULT strItsPlateResult = new NET_ITS_PLATE_RESULT();
                    strItsPlateResult.write();
                    Pointer pItsPlateInfo = strItsPlateResult.getPointer();
                    pItsPlateInfo.write(0, pAlarmInfo.getByteArray(0, strItsPlateResult.size()), 0, strItsPlateResult.size());
                    strItsPlateResult.read();
                    try {
                        String srt3 = new String(strItsPlateResult.struPlateInfo.sLicense, "GBK");
                        sAlarmType = sAlarmType + ",车辆类型：" + strItsPlateResult.byVehicleType + ",交通抓拍上传，车牌：" + srt3;
                    } catch (UnsupportedEncodingException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    newRow[0] = dateFormat.format(today);
                    //报警类型
                    newRow[1] = sAlarmType;
                    //报警设备IP地址
                    sIP = new String(pAlarmer.sDeviceIP).split("\0", 2);
                    newRow[2] = sIP[0];
                    //alarmTableModel.insertRow(0, newRow);

                    for (int i = 0; i < strItsPlateResult.dwPicNum; i++) {
                        if (strItsPlateResult.struPicInfo[i].dwDataLen > 0) {
                            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                            String newName = sf.format(new Date());
                            FileOutputStream fout;
                            try {
                                String filename = ".\\pic\\" + new String(pAlarmer.sDeviceIP).trim() + "_"
                                        + newName + "_type[" + strItsPlateResult.struPicInfo[i].byType + "]_ItsPlate.jpg";
                                fout = new FileOutputStream(filename);
                                //将字节写入文件
                                long offset = 0;
                                ByteBuffer buffers = strItsPlateResult.struPicInfo[i].pBuffer.getByteBuffer(offset, strItsPlateResult.struPicInfo[i].dwDataLen);
                                byte[] bytes = new byte[strItsPlateResult.struPicInfo[i].dwDataLen];
                                buffers.rewind();
                                buffers.get(bytes);
                                fout.write(bytes);
                                fout.close();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                    log.info("交通抓拍的终端图片上传：{}", sAlarmType);
                    break;
                case HikConstant.COMM_ALARM_PDC:
                    NET_DVR_PDC_ALRAM_INFO strPDCResult = new NET_DVR_PDC_ALRAM_INFO();
                    strPDCResult.write();
                    Pointer pPDCInfo = strPDCResult.getPointer();
                    pPDCInfo.write(0, pAlarmInfo.getByteArray(0, strPDCResult.size()), 0, strPDCResult.size());
                    strPDCResult.read();

                    if (strPDCResult.byMode == 0) {
                        strPDCResult.uStatModeParam.setType(NET_DVR_STATFRAME.class);
                        sAlarmType = sAlarmType + "：客流量统计，进入人数：" + strPDCResult.dwEnterNum + "，离开人数：" + strPDCResult.dwLeaveNum +
                                ", byMode:" + strPDCResult.byMode + ", dwRelativeTime:" + strPDCResult.uStatModeParam.struStatFrame.dwRelativeTime +
                                ", dwAbsTime:" + strPDCResult.uStatModeParam.struStatFrame.dwAbsTime;
                    }
                    if (strPDCResult.byMode == 1) {
                        strPDCResult.uStatModeParam.setType(NET_DVR_STATTIME.class);
                        String strtmStart = "" + String.format("%04d", strPDCResult.uStatModeParam.struStatTime.tmStart.dwYear) +
                                String.format("%02d", strPDCResult.uStatModeParam.struStatTime.tmStart.dwMonth) +
                                String.format("%02d", strPDCResult.uStatModeParam.struStatTime.tmStart.dwDay) +
                                String.format("%02d", strPDCResult.uStatModeParam.struStatTime.tmStart.dwHour) +
                                String.format("%02d", strPDCResult.uStatModeParam.struStatTime.tmStart.dwMinute) +
                                String.format("%02d", strPDCResult.uStatModeParam.struStatTime.tmStart.dwSecond);
                        String strtmEnd = "" + String.format("%04d", strPDCResult.uStatModeParam.struStatTime.tmEnd.dwYear) +
                                String.format("%02d", strPDCResult.uStatModeParam.struStatTime.tmEnd.dwMonth) +
                                String.format("%02d", strPDCResult.uStatModeParam.struStatTime.tmEnd.dwDay) +
                                String.format("%02d", strPDCResult.uStatModeParam.struStatTime.tmEnd.dwHour) +
                                String.format("%02d", strPDCResult.uStatModeParam.struStatTime.tmEnd.dwMinute) +
                                String.format("%02d", strPDCResult.uStatModeParam.struStatTime.tmEnd.dwSecond);
                        sAlarmType = sAlarmType + "：客流量统计，进入人数：" + strPDCResult.dwEnterNum + "，离开人数：" + strPDCResult.dwLeaveNum +
                                ", byMode:" + strPDCResult.byMode + ", tmStart:" + strtmStart + ",tmEnd :" + strtmEnd;
                    }

                    newRow[0] = dateFormat.format(today);
                    //报警类型
                    newRow[1] = sAlarmType;
                    //报警设备IP地址
                    sIP = new String(strPDCResult.struDevInfo.struDevIP.sIpV4).split("\0", 2);
                    newRow[2] = sIP[0];
                    //alarmTableModel.insertRow(0, newRow);
                    log.info("客流量统计报警上传：{}", sAlarmType);
                    break;

                case HikConstant.COMM_ITS_PARK_VEHICLE:
                    NET_ITS_PARK_VEHICLE strItsParkVehicle = new NET_ITS_PARK_VEHICLE();
                    strItsParkVehicle.write();
                    Pointer pItsParkVehicle = strItsParkVehicle.getPointer();
                    pItsParkVehicle.write(0, pAlarmInfo.getByteArray(0, strItsParkVehicle.size()), 0, strItsParkVehicle.size());
                    strItsParkVehicle.read();
                    try {
                        String srtParkingNo = new String(strItsParkVehicle.byParkingNo).trim(); //车位编号
                        String srtPlate = new String(strItsParkVehicle.struPlateInfo.sLicense, "GBK").trim(); //车牌号码
                        sAlarmType = sAlarmType + ",停产场数据,车位编号：" + srtParkingNo + ",车位状态："
                                + strItsParkVehicle.byLocationStatus + ",车牌：" + srtPlate;
                    } catch (UnsupportedEncodingException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    newRow[0] = dateFormat.format(today);
                    //报警类型
                    newRow[1] = sAlarmType;
                    //报警设备IP地址
                    sIP = new String(pAlarmer.sDeviceIP).split("\0", 2);
                    newRow[2] = sIP[0];
                    //alarmTableModel.insertRow(0, newRow);

                    for (int i = 0; i < strItsParkVehicle.dwPicNum; i++) {
                        if (strItsParkVehicle.struPicInfo[i].dwDataLen > 0) {
                            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                            String newName = sf.format(new Date());
                            FileOutputStream fout;
                            try {
                                String filename = ".\\pic\\" + new String(pAlarmer.sDeviceIP).trim() + "_"
                                        + newName + "_type[" + strItsParkVehicle.struPicInfo[i].byType + "]_ParkVehicle.jpg";
                                fout = new FileOutputStream(filename);
                                //将字节写入文件
                                long offset = 0;
                                ByteBuffer buffers = strItsParkVehicle.struPicInfo[i].pBuffer.getByteBuffer(offset, strItsParkVehicle.struPicInfo[i].dwDataLen);
                                byte[] bytes = new byte[strItsParkVehicle.struPicInfo[i].dwDataLen];
                                buffers.rewind();
                                buffers.get(bytes);
                                fout.write(bytes);
                                fout.close();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                    log.info("停车场数据上传：{}", sAlarmType);
                    break;
                case HikConstant.COMM_ALARM_TFS:
                    NET_DVR_TFS_ALARM strTFSAlarmInfo = new NET_DVR_TFS_ALARM();
                    strTFSAlarmInfo.write();
                    Pointer pTFSInfo = strTFSAlarmInfo.getPointer();
                    pTFSInfo.write(0, pAlarmInfo.getByteArray(0, strTFSAlarmInfo.size()), 0, strTFSAlarmInfo.size());
                    strTFSAlarmInfo.read();

                    try {
                        String srtPlate = new String(strTFSAlarmInfo.struPlateInfo.sLicense, "GBK").trim(); //车牌号码
                        sAlarmType = sAlarmType + "：交通取证报警信息，违章类型：" + strTFSAlarmInfo.dwIllegalType + "，车牌号码：" + srtPlate
                                + "，车辆出入状态：" + strTFSAlarmInfo.struAIDInfo.byVehicleEnterState;
                    } catch (UnsupportedEncodingException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    newRow[0] = dateFormat.format(today);
                    //报警类型
                    newRow[1] = sAlarmType;
                    //报警设备IP地址
                    sIP = new String(strTFSAlarmInfo.struDevInfo.struDevIP.sIpV4).split("\0", 2);
                    newRow[2] = sIP[0];
                    //alarmTableModel.insertRow(0, newRow);
                    log.info("交通取证报警信息：{}", sAlarmType);
                    break;
                case HikConstant.COMM_ALARM_AID_V41:
                    NET_DVR_AID_ALARM_V41 struAIDAlarmInfo = new NET_DVR_AID_ALARM_V41();
                    struAIDAlarmInfo.write();
                    Pointer pAIDInfo = struAIDAlarmInfo.getPointer();
                    pAIDInfo.write(0, pAlarmInfo.getByteArray(0, struAIDAlarmInfo.size()), 0, struAIDAlarmInfo.size());
                    struAIDAlarmInfo.read();
                    sAlarmType = sAlarmType + "：交通事件报警信息，交通事件类型：" + struAIDAlarmInfo.struAIDInfo.dwAIDType + "，规则ID："
                            + struAIDAlarmInfo.struAIDInfo.byRuleID + "，车辆出入状态：" + struAIDAlarmInfo.struAIDInfo.byVehicleEnterState;

                    newRow[0] = dateFormat.format(today);
                    //报警类型
                    newRow[1] = sAlarmType;
                    //报警设备IP地址
                    sIP = new String(struAIDAlarmInfo.struDevInfo.struDevIP.sIpV4).split("\0", 2);
                    newRow[2] = sIP[0];
                    //alarmTableModel.insertRow(0, newRow);
                    log.info("交通事件报警信息扩展：{}", sAlarmType);
                    break;
                case HikConstant.COMM_ALARM_TPS_V41:
                    NET_DVR_TPS_ALARM_V41 struTPSAlarmInfo = new NET_DVR_TPS_ALARM_V41();
                    struTPSAlarmInfo.write();
                    Pointer pTPSInfo = struTPSAlarmInfo.getPointer();
                    pTPSInfo.write(0, pAlarmInfo.getByteArray(0, struTPSAlarmInfo.size()), 0, struTPSAlarmInfo.size());
                    struTPSAlarmInfo.read();

                    sAlarmType = sAlarmType + "：交通统计报警信息，绝对时标：" + struTPSAlarmInfo.dwAbsTime
                            + "，能见度:" + struTPSAlarmInfo.struDevInfo.byIvmsChannel
                            + "，车道1交通状态:" + struTPSAlarmInfo.struTPSInfo.struLaneParam[0].byTrafficState
                            + "，监测点编号：" + new String(struTPSAlarmInfo.byMonitoringSiteID).trim()
                            + "，设备编号：" + new String(struTPSAlarmInfo.byDeviceID).trim()
                            + "，开始统计时间：" + struTPSAlarmInfo.dwStartTime
                            + "，结束统计时间：" + struTPSAlarmInfo.dwStopTime;

                    newRow[0] = dateFormat.format(today);
                    //报警类型
                    newRow[1] = sAlarmType;
                    //报警设备IP地址
                    sIP = new String(struTPSAlarmInfo.struDevInfo.struDevIP.sIpV4).split("\0", 2);
                    newRow[2] = sIP[0];
                    //alarmTableModel.insertRow(0, newRow);
                    log.info("交通事件报警信息扩展：{}", sAlarmType);
                    break;
                case HikConstant.COMM_UPLOAD_FACESNAP_RESULT:
                    //实时人脸抓拍上传
                    NET_VCA_FACESNAP_RESULT strFaceSnapInfo = new NET_VCA_FACESNAP_RESULT();
                    strFaceSnapInfo.write();
                    Pointer pFaceSnapInfo = strFaceSnapInfo.getPointer();
                    pFaceSnapInfo.write(0, pAlarmInfo.getByteArray(0, strFaceSnapInfo.size()), 0, strFaceSnapInfo.size());
                    strFaceSnapInfo.read();
                    sAlarmType = sAlarmType + "：人脸抓拍上传，人脸评分：" + strFaceSnapInfo.dwFaceScore + "，年龄段：" + strFaceSnapInfo.struFeature.byAgeGroup + "，性别：" + strFaceSnapInfo.struFeature.bySex;
                    newRow[0] = dateFormat.format(today);
                    //报警类型
                    newRow[1] = sAlarmType;
                    //报警设备IP地址
                    sIP = new String(strFaceSnapInfo.struDevInfo.struDevIP.sIpV4).split("\0", 2);
                    newRow[2] = sIP[0];
                    //alarmTableModel.insertRow(0, newRow);
                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss"); //设置日期格式
                    String time = df.format(new Date()); // new Date()为获取当前系统时间
                    //人脸图片写文件
                    try {
                        FileOutputStream small = new FileOutputStream(System.getProperty("user.dir") + "\\pic\\" + time + "small.jpg");
                        FileOutputStream big = new FileOutputStream(System.getProperty("user.dir") + "\\pic\\" + time + "big.jpg");

                        if (strFaceSnapInfo.dwFacePicLen > 0) {
                            try {
                                small.write(strFaceSnapInfo.pBuffer1.getByteArray(0, strFaceSnapInfo.dwFacePicLen), 0, strFaceSnapInfo.dwFacePicLen);
                                small.close();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }

                        }
                        if (strFaceSnapInfo.dwFacePicLen > 0) {
                            try {
                                big.write(strFaceSnapInfo.pBuffer2.getByteArray(0, strFaceSnapInfo.dwBackgroundPicLen), 0, strFaceSnapInfo.dwBackgroundPicLen);
                                big.close();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                    log.info("人脸识别结果上传：{}", sAlarmType);
                    break;
                case HikConstant.COMM_SNAP_MATCH_ALARM:
                    //人脸名单比对报警
                    NET_VCA_FACESNAP_MATCH_ALARM strFaceSnapMatch = new NET_VCA_FACESNAP_MATCH_ALARM();
                    strFaceSnapMatch.write();
                    Pointer pFaceSnapMatch = strFaceSnapMatch.getPointer();
                    pFaceSnapMatch.write(0, pAlarmInfo.getByteArray(0, strFaceSnapMatch.size()), 0, strFaceSnapMatch.size());
                    strFaceSnapMatch.read();

                    if ((strFaceSnapMatch.dwSnapPicLen > 0) && (strFaceSnapMatch.byPicTransType == 0)) {
                        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                        String newName = sf.format(new Date());
                        FileOutputStream fout;
                        try {
                            String filename = System.getProperty("user.dir") + "\\pic\\" + newName + "_pSnapPicBuffer" + ".jpg";
                            fout = new FileOutputStream(filename);
                            //将字节写入文件
                            long offset = 0;
                            ByteBuffer buffers = strFaceSnapMatch.pSnapPicBuffer.getByteBuffer(offset, strFaceSnapMatch.dwSnapPicLen);
                            byte[] bytes = new byte[strFaceSnapMatch.dwSnapPicLen];
                            buffers.rewind();
                            buffers.get(bytes);
                            fout.write(bytes);
                            fout.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    if ((strFaceSnapMatch.struSnapInfo.dwSnapFacePicLen > 0) && (strFaceSnapMatch.byPicTransType == 0)) {
                        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                        String newName = sf.format(new Date());
                        FileOutputStream fout;
                        try {
                            String filename = System.getProperty("user.dir") + "\\pic\\" + newName + "_struSnapInfo_pBuffer1" + ".jpg";
                            fout = new FileOutputStream(filename);
                            //将字节写入文件
                            long offset = 0;
                            ByteBuffer buffers = strFaceSnapMatch.struSnapInfo.pBuffer1.getByteBuffer(offset, strFaceSnapMatch.struSnapInfo.dwSnapFacePicLen);
                            byte[] bytes = new byte[strFaceSnapMatch.struSnapInfo.dwSnapFacePicLen];
                            buffers.rewind();
                            buffers.get(bytes);
                            fout.write(bytes);
                            fout.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    if ((strFaceSnapMatch.struBlockListInfo.dwBlockListPicLen > 0) && (strFaceSnapMatch.byPicTransType == 0)) {
                        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                        String newName = sf.format(new Date());
                        FileOutputStream fout;
                        try {
                            String filename = System.getProperty("user.dir") + "\\pic\\" + newName + "_fSimilarity_" + strFaceSnapMatch.fSimilarity + "_struBlockListInfo_pBuffer1" + ".jpg";
                            fout = new FileOutputStream(filename);
                            //将字节写入文件
                            long offset = 0;
                            ByteBuffer buffers = strFaceSnapMatch.struBlockListInfo.pBuffer1.getByteBuffer(offset, strFaceSnapMatch.struBlockListInfo.dwBlockListPicLen);
                            byte[] bytes = new byte[strFaceSnapMatch.struBlockListInfo.dwBlockListPicLen];
                            buffers.rewind();
                            buffers.get(bytes);
                            fout.write(bytes);
                            fout.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    sAlarmType = sAlarmType + "：人脸名单比对报警，相识度：" + strFaceSnapMatch.fSimilarity + "，名单姓名：" + new String(strFaceSnapMatch.struBlockListInfo.struBlockListInfo.struAttribute.byName, "GBK").trim() + "，\n名单证件信息：" + new String(strFaceSnapMatch.struBlockListInfo.struBlockListInfo.struAttribute.byCertificateNumber).trim();

                    //获取人脸库ID
                    byte[] FDIDbytes;
                    if ((strFaceSnapMatch.struBlockListInfo.dwFDIDLen > 0) && (strFaceSnapMatch.struBlockListInfo.pFDID != null)) {
                        ByteBuffer FDIDbuffers = strFaceSnapMatch.struBlockListInfo.pFDID.getByteBuffer(0, strFaceSnapMatch.struBlockListInfo.dwFDIDLen);
                        FDIDbytes = new byte[strFaceSnapMatch.struBlockListInfo.dwFDIDLen];
                        FDIDbuffers.rewind();
                        FDIDbuffers.get(FDIDbytes);
                        sAlarmType = sAlarmType + "，人脸库ID:" + new String(FDIDbytes).trim();
                    }
                    //获取人脸图片ID
                    byte[] PIDbytes;
                    if ((strFaceSnapMatch.struBlockListInfo.dwPIDLen > 0) && (strFaceSnapMatch.struBlockListInfo.pPID != null)) {
                        ByteBuffer PIDbuffers = strFaceSnapMatch.struBlockListInfo.pPID.getByteBuffer(0, strFaceSnapMatch.struBlockListInfo.dwPIDLen);
                        PIDbytes = new byte[strFaceSnapMatch.struBlockListInfo.dwPIDLen];
                        PIDbuffers.rewind();
                        PIDbuffers.get(PIDbytes);
                        sAlarmType = sAlarmType + "，人脸图片ID:" + new String(PIDbytes).trim();
                    }
                    newRow[0] = dateFormat.format(today);
                    //报警类型
                    newRow[1] = sAlarmType;
                    //报警设备IP地址
                    sIP = new String(pAlarmer.sDeviceIP).split("\0", 2);
                    newRow[2] = sIP[0];
                    //alarmTableModel.insertRow(0, newRow);
                    log.info("人脸比对结果上传：{}", sAlarmType);
                    break;
                case HikConstant.COMM_ALARM_ACS: //门禁主机报警信息
                    NET_DVR_ACS_ALARM_INFO strACSInfo = new NET_DVR_ACS_ALARM_INFO();
                    strACSInfo.write();
                    Pointer pACSInfo = strACSInfo.getPointer();
                    pACSInfo.write(0, pAlarmInfo.getByteArray(0, strACSInfo.size()), 0, strACSInfo.size());
                    strACSInfo.read();

                    String cardNo = new String(strACSInfo.struAcsEventInfo.byCardNo).trim();
                    int employeeNo = strACSInfo.struAcsEventInfo.dwEmployeeNo;
                    NET_DVR_TIME struTime = strACSInfo.struTime;
                    String deviceIp = new String(pAlarmer.sDeviceIP).trim();
                    String eventTime = struTime.dwYear + "-" + struTime.dwMonth + "-" + struTime.dwDay + " " + struTime.dwHour + ":" + struTime.dwMinute + ":" + struTime.dwSecond;
                    if (strACSInfo.dwPicDataLen > 0) {
                        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                        String picDate = sdf.format(new Date());
                        FileOutputStream fout;
                        String pathname = "./pic/";
                        try {
                            File file = new File(pathname);
                            if (!file.isDirectory()) {
                                log.info("没有那个目录，所以创建目录");
                                file.mkdirs();
                            }
                            pathname += deviceIp + "_byCardNo[" + cardNo + "_" + picDate + "_Acs.jpg";
                            fout = new FileOutputStream(pathname);
                            //将字节写入文件
                            long offset = 0;
                            ByteBuffer buffers = strACSInfo.pPicData.getByteBuffer(offset, strACSInfo.dwPicDataLen);
                            byte[] bytes = new byte[strACSInfo.dwPicDataLen];
                            buffers.rewind();
                            buffers.get(bytes);
                            fout.write(bytes);
                            fout.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        String uploadFile = CloudUploadUtil.uploadFile(new File(pathname));
                        JSONObject data = new JSONObject();
                        data.put("cardNo", cardNo);
                        data.put("employeeNo", employeeNo);
                        data.put("deviceIp", deviceIp);
                        data.put("eventTime", eventTime);
                        data.put("majorAlarmType", strACSInfo.dwMajor);
                        data.put("minorAlarmType", strACSInfo.dwMinor);
                        data.put("cardType", strACSInfo.struAcsEventInfo.byCardType);
                        data.put("uploadFile", uploadFile);
                        CloudUploadUtil.sendDataToCloudApi(data);
                        log.info("门禁主机报警信息=====>{}", data);
                    }
                    log.info("门禁主机报警信息：{}", sAlarmType);
                    break;
                case HikConstant.COMM_ID_INFO_ALARM: //身份证信息
                    NET_DVR_ID_CARD_INFO_ALARM strIDCardInfo = new NET_DVR_ID_CARD_INFO_ALARM();
                    strIDCardInfo.write();
                    Pointer pIDCardInfo = strIDCardInfo.getPointer();
                    pIDCardInfo.write(0, pAlarmInfo.getByteArray(0, strIDCardInfo.size()), 0, strIDCardInfo.size());
                    strIDCardInfo.read();

                    sAlarmType = sAlarmType + "：门禁身份证刷卡信息，身份证号码：" + new String(strIDCardInfo.struIDCardCfg.byIDNum).trim() + "，姓名：" +
                            new String(strIDCardInfo.struIDCardCfg.byName).trim() + "，报警主类型：" + strIDCardInfo.dwMajor + "，报警次类型：" + strIDCardInfo.dwMinor;

                    newRow[0] = dateFormat.format(today);
                    //报警类型
                    newRow[1] = sAlarmType;
                    //报警设备IP地址
                    sIP = new String(pAlarmer.sDeviceIP).split("\0", 2);
                    newRow[2] = sIP[0];
                    //alarmTableModel.insertRow(0, newRow);

                    //身份证图片
                    if (strIDCardInfo.dwPicDataLen > 0) {
                        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                        String newName = sf.format(new Date());
                        FileOutputStream fout;
                        try {
                            String filename = ".\\pic\\" + new String(pAlarmer.sDeviceIP).trim() +
                                    "_byCardNo[" + new String(strIDCardInfo.struIDCardCfg.byIDNum).trim() +
                                    "_" + newName + "_IDInfoPic.jpg";
                            fout = new FileOutputStream(filename);
                            //将字节写入文件
                            long offset = 0;
                            ByteBuffer buffers = strIDCardInfo.pPicData.getByteBuffer(offset, strIDCardInfo.dwPicDataLen);
                            byte[] bytes = new byte[strIDCardInfo.dwPicDataLen];
                            buffers.rewind();
                            buffers.get(bytes);
                            fout.write(bytes);
                            fout.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    //抓拍图片
                    if (strIDCardInfo.dwCapturePicDataLen > 0) {
                        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                        String newName = sf.format(new Date());
                        FileOutputStream fout;
                        try {
                            String filename = ".\\pic\\" + new String(pAlarmer.sDeviceIP).trim() +
                                    "_byCardNo[" + new String(strIDCardInfo.struIDCardCfg.byIDNum).trim() +
                                    "_" + newName + "_IDInfoCapturePic.jpg";
                            fout = new FileOutputStream(filename);
                            //将字节写入文件
                            long offset = 0;
                            ByteBuffer buffers = strIDCardInfo.pCapturePicData.getByteBuffer(offset, strIDCardInfo.dwCapturePicDataLen);
                            byte[] bytes = new byte[strIDCardInfo.dwCapturePicDataLen];
                            buffers.rewind();
                            buffers.get(bytes);
                            fout.write(bytes);
                            fout.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    log.info("门禁身份证刷卡信息：{}", sAlarmType);
                    break;
                case HikConstant.COMM_UPLOAD_AIOP_VIDEO: //设备支持AI开放平台接入，上传视频检测数据
                    NET_AIOP_VIDEO_HEAD struAIOPVideo = new NET_AIOP_VIDEO_HEAD();
                    struAIOPVideo.write();
                    Pointer pAIOPVideo = struAIOPVideo.getPointer();
                    pAIOPVideo.write(0, pAlarmInfo.getByteArray(0, struAIOPVideo.size()), 0, struAIOPVideo.size());
                    struAIOPVideo.read();

                    String strTime = "" + String.format("%04d", struAIOPVideo.struTime.wYear) +
                            String.format("%02d", struAIOPVideo.struTime.wMonth) +
                            String.format("%02d", struAIOPVideo.struTime.wDay) +
                            String.format("%02d", struAIOPVideo.struTime.wHour) +
                            String.format("%02d", struAIOPVideo.struTime.wMinute) +
                            String.format("%02d", struAIOPVideo.struTime.wSecond) +
                            String.format("%03d", struAIOPVideo.struTime.wMilliSec);

                    sAlarmType = sAlarmType + "：AI开放平台接入，上传视频检测数据，通道号:" + struAIOPVideo.dwChannel +
                            ", 时间:" + strTime;

                    newRow[0] = dateFormat.format(today);
                    //报警类型
                    newRow[1] = sAlarmType;
                    //报警设备IP地址
                    sIP = new String(pAlarmer.sDeviceIP).split("\0", 2);
                    newRow[2] = sIP[0];
                    //alarmTableModel.insertRow(0, newRow);

                    if (struAIOPVideo.dwAIOPDataSize > 0) {
                        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                        String newName = sf.format(new Date());
                        FileOutputStream fout;
                        try {
                            String filename = ".\\pic\\" + new String(pAlarmer.sDeviceIP).trim() +
                                    "_" + newName + "_AIO_VideoData.json";
                            fout = new FileOutputStream(filename);
                            //将字节写入文件
                            long offset = 0;
                            ByteBuffer buffers = struAIOPVideo.pBufferAIOPData.getByteBuffer(offset, struAIOPVideo.dwAIOPDataSize);
                            byte[] bytes = new byte[struAIOPVideo.dwAIOPDataSize];
                            buffers.rewind();
                            buffers.get(bytes);
                            fout.write(bytes);
                            fout.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    if (struAIOPVideo.dwPictureSize > 0) {
                        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                        String newName = sf.format(new Date());
                        FileOutputStream fout;
                        try {
                            String filename = ".\\pic\\" + new String(pAlarmer.sDeviceIP).trim() +
                                    "_" + newName + "_AIO_VideoPic.jpg";
                            fout = new FileOutputStream(filename);
                            //将字节写入文件
                            long offset = 0;
                            ByteBuffer buffers = struAIOPVideo.pBufferPicture.getByteBuffer(offset, struAIOPVideo.dwPictureSize);
                            byte[] bytes = new byte[struAIOPVideo.dwPictureSize];
                            buffers.rewind();
                            buffers.get(bytes);
                            fout.write(bytes);
                            fout.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    log.info("设备支持AI开放平台接入，上传视频检测数据：{}", sAlarmType);
                    break;
                case HikConstant.COMM_UPLOAD_AIOP_PICTURE: //设备支持AI开放平台接入，上传视频检测数据
                    NET_AIOP_PICTURE_HEAD struAIOPPic = new NET_AIOP_PICTURE_HEAD();
                    struAIOPPic.write();
                    Pointer pAIOPPic = struAIOPPic.getPointer();
                    pAIOPPic.write(0, pAlarmInfo.getByteArray(0, struAIOPPic.size()), 0, struAIOPPic.size());
                    struAIOPPic.read();

                    String strPicTime = "" + String.format("%04d", struAIOPPic.struTime.wYear) +
                            String.format("%02d", struAIOPPic.struTime.wMonth) +
                            String.format("%02d", struAIOPPic.struTime.wDay) +
                            String.format("%02d", struAIOPPic.struTime.wHour) +
                            String.format("%02d", struAIOPPic.struTime.wMinute) +
                            String.format("%02d", struAIOPPic.struTime.wSecond) +
                            String.format("%03d", struAIOPPic.struTime.wMilliSec);

                    sAlarmType = sAlarmType + "：AI开放平台接入，上传图片检测数据，通道号:" + new String(struAIOPPic.szPID) +
                            ", 时间:" + strPicTime;

                    newRow[0] = dateFormat.format(today);
                    //报警类型
                    newRow[1] = sAlarmType;
                    //报警设备IP地址
                    sIP = new String(pAlarmer.sDeviceIP).split("\0", 2);
                    newRow[2] = sIP[0];
                    //alarmTableModel.insertRow(0, newRow);

                    if (struAIOPPic.dwAIOPDataSize > 0) {
                        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                        String newName = sf.format(new Date());
                        FileOutputStream fout;
                        try {
                            String filename = ".\\pic\\" + new String(pAlarmer.sDeviceIP).trim() +
                                    "_" + newName + "_AIO_PicData.json";
                            fout = new FileOutputStream(filename);
                            //将字节写入文件
                            long offset = 0;
                            ByteBuffer buffers = struAIOPPic.pBufferAIOPData.getByteBuffer(offset, struAIOPPic.dwAIOPDataSize);
                            byte[] bytes = new byte[struAIOPPic.dwAIOPDataSize];
                            buffers.rewind();
                            buffers.get(bytes);
                            fout.write(bytes);
                            fout.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    log.info("设备支持AI开放平台接入，上传图片检测数据：{}", sAlarmType);
                    break;
                case HikConstant.COMM_ISAPI_ALARM: //ISAPI协议报警信息
                    NET_DVR_ALARM_ISAPI_INFO struEventISAPI = new NET_DVR_ALARM_ISAPI_INFO();
                    struEventISAPI.write();
                    Pointer pEventISAPI = struEventISAPI.getPointer();
                    pEventISAPI.write(0, pAlarmInfo.getByteArray(0, struEventISAPI.size()), 0, struEventISAPI.size());
                    struEventISAPI.read();

                    sAlarmType = sAlarmType + "：ISAPI协议报警信息, 数据格式:" + struEventISAPI.byDataType +
                            ", 图片个数:" + struEventISAPI.byPicturesNumber;

                    newRow[0] = dateFormat.format(today);
                    //报警类型
                    newRow[1] = sAlarmType;
                    //报警设备IP地址
                    sIP = new String(pAlarmer.sDeviceIP).split("\0", 2);
                    newRow[2] = sIP[0];
                    //alarmTableModel.insertRow(0, newRow);

                    SimpleDateFormat sf1 = new SimpleDateFormat("yyyyMMddHHmmss");
                    String curTime = sf1.format(new Date());
                    FileOutputStream foutdata;
                    try {
                        String jsonfilename = ".\\pic\\" + new String(pAlarmer.sDeviceIP).trim() + curTime + "_ISAPI_Alarm_" + ".json";
                        foutdata = new FileOutputStream(jsonfilename);
                        //将字节写入文件
                        ByteBuffer jsonbuffers = struEventISAPI.pAlarmData.getByteBuffer(0, struEventISAPI.dwAlarmDataLen);
                        byte[] jsonbytes = new byte[struEventISAPI.dwAlarmDataLen];
                        jsonbuffers.rewind();
                        jsonbuffers.get(jsonbytes);
                        foutdata.write(jsonbytes);
                        foutdata.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    for (int i = 0; i < struEventISAPI.byPicturesNumber; i++) {
                        NET_DVR_ALARM_ISAPI_PICDATA struPicData = new NET_DVR_ALARM_ISAPI_PICDATA();
                        struPicData.write();
                        Pointer pPicData = struPicData.getPointer();
                        pPicData.write(0, struEventISAPI.pPicPackData.getByteArray(i * struPicData.size(), struPicData.size()), 0, struPicData.size());
                        struPicData.read();

                        FileOutputStream fout;
                        try {
                            String filename = ".\\pic\\" + new String(pAlarmer.sDeviceIP).trim() + curTime +
                                    "_ISAPIPic_" + i + "_" + new String(struPicData.szFilename).trim() + ".jpg";
                            fout = new FileOutputStream(filename);
                            //将字节写入文件
                            long offset = 0;
                            ByteBuffer buffers = struPicData.pPicData.getByteBuffer(offset, struPicData.dwPicLen);
                            byte[] bytes = new byte[struPicData.dwPicLen];
                            buffers.rewind();
                            buffers.get(bytes);
                            fout.write(bytes);
                            fout.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    log.info("ISAPI协议报警信息：{}", sAlarmType);
                    break;
                default:
                    newRow[0] = dateFormat.format(today);
                    //报警类型
                    newRow[1] = sAlarmType;
                    //报警设备IP地址
                    sIP = new String(pAlarmer.sDeviceIP).split("\0", 2);
                    newRow[2] = sIP[0];
                    //alarmTableModel.insertRow(0, newRow);
                    log.info("其他信息：{},lCommand是传的报警类型:{}", sAlarmType, lCommand);
                    break;
            }
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
    }
}
