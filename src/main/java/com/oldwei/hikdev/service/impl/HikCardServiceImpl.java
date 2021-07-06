package com.oldwei.hikdev.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.oldwei.hikdev.constant.HikConstant;
import com.oldwei.hikdev.constant.DataCachePrefixConstant;
import com.oldwei.hikdev.service.IHikCardService;
import com.oldwei.hikdev.service.IHikDevService;
import com.oldwei.hikdev.structure.*;
import com.oldwei.hikdev.util.DataCache;
import com.oldwei.hikdev.util.StringEncodingUtil;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author oldwei
 * @date 2021-5-14 17:16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HikCardServiceImpl implements IHikCardService {

    private final DataCache dataCache;
    private final IHikDevService hikDevService;

    @Override
    public JSONObject selectFaceByCardNo(JSONObject jsonObject) {
        JSONObject cardData = new JSONObject();
        cardData.put("event", jsonObject.getString("event"));
        String strCardNo = jsonObject.getString("cardNo");
        String ip = jsonObject.getString("ip");
        Integer longUserId = this.dataCache.getInteger(DataCachePrefixConstant.HIK_REG_USERID_IP + ip);
        if (null == longUserId) {
            cardData.put("code", -1);
            cardData.put("msg", "设备状态未注册！");
            return cardData;
        }
        NET_DVR_FACE_COND struFaceCond = new NET_DVR_FACE_COND();
        struFaceCond.read();
        struFaceCond.dwSize = struFaceCond.size();
        //查询一个人脸参数
        struFaceCond.dwFaceNum = 1;
        //读卡器编号
        struFaceCond.dwEnableReaderNo = 1;

        for (int j = 0; j < HikConstant.ACS_CARD_NO_LEN; j++) {
            struFaceCond.byCardNo[j] = 0;
        }
        System.arraycopy(strCardNo.getBytes(), 0, struFaceCond.byCardNo, 0, strCardNo.getBytes().length);

        struFaceCond.write();
        int m_lHandle = this.hikDevService.NET_DVR_StartRemoteConfig(longUserId, HikConstant.NET_DVR_GET_FACE, struFaceCond.getPointer(), struFaceCond.size(), null, null);
        if (m_lHandle == -1) {
            cardData.put("code", -1);
            cardData.put("msg", "建立查询人脸参数长连接失败，错误码为" + this.hikDevService.NET_DVR_GetLastError());
            return cardData;
        } else {
            log.info("建立查询人脸参数长连接成功！");
        }

        //查询结果
        NET_DVR_FACE_RECORD struFaceRecord = new NET_DVR_FACE_RECORD();
        struFaceRecord.read();

        List<String> list = new ArrayList<>();
        while (true) {
            int dwState = this.hikDevService.NET_DVR_GetNextRemoteConfig(m_lHandle, struFaceRecord.getPointer(), struFaceRecord.size());
            struFaceRecord.read();
            if (dwState == -1) {
                cardData.put("code", -1);
                cardData.put("msg", "NET_DVR_GetNextRemoteConfig查询人脸调用失败，错误码：" + this.hikDevService.NET_DVR_GetLastError());
                log.info("NET_DVR_GetNextRemoteConfig查询人脸调用失败，错误码：" + this.hikDevService.NET_DVR_GetLastError());
                break;
            } else if (dwState == HikConstant.NET_SDK_CONFIG_STATUS_NEEDWAIT) {
                log.info("查询中，请等待...");
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if (dwState == HikConstant.NET_SDK_CONFIG_STATUS_FAILED) {
                cardData.put("code", -1);
                cardData.put("msg", "获取人脸参数失败, 卡号: " + strCardNo);
                log.info("获取人脸参数失败, 卡号: " + strCardNo);
                break;
            } else if (dwState == HikConstant.NET_SDK_CONFIG_STATUS_SUCCESS) {
                if ((struFaceRecord.dwFaceLen > 0) && (struFaceRecord.pFaceBuffer != null)) {
                    FileOutputStream fout;
                    try {
                        String filename = System.getProperty("user.dir") + "\\pic\\menjin_" + strCardNo + "_FaceData.jpg";
                        fout = new FileOutputStream(filename);
                        //将字节写入文件
                        long offset = 0;
                        ByteBuffer buffers = struFaceRecord.pFaceBuffer.getByteBuffer(offset, struFaceRecord.dwFaceLen);
                        byte[] bytes = new byte[struFaceRecord.dwFaceLen];
                        buffers.rewind();
                        buffers.get(bytes);
                        fout.write(bytes);
                        fout.close();
                        File file = new File(filename);
                        String encodeFile = Base64.encode(file);
                        list.add(encodeFile);
                        log.info("获取人脸参数成功, 卡号: " + strCardNo + ", 图片保存路径: " + filename);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (dwState == HikConstant.NET_SDK_CONFIG_STATUS_FINISH) {
                cardData.put("code", -1);
                cardData.put("msg", "获取人脸参数完成");
                log.info("获取人脸参数完成");
                break;
            }
        }
        if (!this.hikDevService.NET_DVR_StopRemoteConfig(m_lHandle)) {
            cardData.put("code", -1);
            cardData.put("msg", "NET_DVR_StopRemoteConfig接口调用失败，错误码：" + this.hikDevService.NET_DVR_GetLastError());
            return cardData;
        } else {
            log.info("NET_DVR_StopRemoteConfig接口成功");
        }
        if (list.size() > 0) {
            cardData.put("code", 1);
            cardData.put("data", list);
            cardData.put("msg", "查询完成！");
        }
        return cardData;
    }

    @Override
    public JSONObject selectCardInfoByDeviceIp(JSONObject jsonObject) {
        JSONObject result = new JSONObject();
        result.put("event", jsonObject.getString("event"));
        String ip = jsonObject.getString("ip");
        if (StrUtil.isBlank(ip)) {
            result.put("code", -1);
            result.put("msg", "缺少必要参数字段：ip");
            return result;
        }
        Integer longUserId = this.dataCache.getInteger(DataCachePrefixConstant.HIK_REG_USERID_IP + ip);
        if (null == longUserId) {
            result.put("code", -1);
            result.put("msg", "设备状态未注册！");
            return result;
        }
        NET_DVR_CARD_COND strCardCond = new NET_DVR_CARD_COND();
        strCardCond.read();
        strCardCond.dwSize = strCardCond.size();
        //查询所有
        strCardCond.dwCardNum = 0xffffffff;
        strCardCond.write();
        Pointer ptrStruCond = strCardCond.getPointer();

        //下发卡长连接句柄
        int setCardConfigHandle = hikDevService.NET_DVR_StartRemoteConfig(longUserId, HikConstant.NET_DVR_GET_CARD, ptrStruCond, strCardCond.size(), null, null);
        if (setCardConfigHandle == -1) {
            result.put("code", -1);
            result.put("msg", "建立下发卡长连接失败，错误码为" + hikDevService.NET_DVR_GetLastError());
            return result;
        } else {
            log.info("建立下发卡长连接成功！");
        }

        NET_DVR_CARD_RECORD struCardRecord = new NET_DVR_CARD_RECORD();
        struCardRecord.read();
        struCardRecord.dwSize = struCardRecord.size();
        struCardRecord.write();

        IntByReference pInt = new IntByReference(0);
        Integer iCharEncodeType = this.dataCache.getInteger(DataCachePrefixConstant.HIK_REG_CHAR_ENCODE_TYPE_IP + ip);
        if (null == iCharEncodeType) {
            iCharEncodeType = 6;
        }
        List<Map<String, Object>> list = new ArrayList<>();
        while (true) {
            //下发卡数据状态
            int dwState = hikDevService.NET_DVR_GetNextRemoteConfig(setCardConfigHandle, struCardRecord.getPointer(), struCardRecord.size());
            struCardRecord.read();
            if (dwState == -1) {
                result.put("code", -1);
                result.put("msg", "NET_DVR_SendWithRecvRemoteConfig接口调用失败，错误码：" + hikDevService.NET_DVR_GetLastError());
                break;
            } else if (dwState == HikConstant.NET_SDK_CONFIG_STATUS_NEEDWAIT) {
                log.info("配置等待");
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                continue;
            } else if (dwState == HikConstant.NET_SDK_CONFIG_STATUS_FAILED) {
                result.put("code", -1);
                result.put("msg", "获取卡参数失败");
                break;
            } else if (dwState == HikConstant.NET_SDK_CONFIG_STATUS_EXCEPTION) {
                result.put("code", -1);
                result.put("msg", "获取卡参数异常");
                break;
            } else if (dwState == HikConstant.NET_SDK_CONFIG_STATUS_SUCCESS) {
                //姓名
                String strName = StringEncodingUtil.guessEncodingTransformString(struCardRecord.byName);
                //卡号
                String cardNo = new String(struCardRecord.byCardNo).trim();
                //卡类型
                byte cardType = struCardRecord.byCardType;
                Map<String, Object> map = new HashMap<>(3);
                map.put("strName", strName);
                map.put("cardNo", cardNo);
                map.put("cardType", cardType);
                map.put("intEmployeeNo", struCardRecord.dwEmployeeNo);
                list.add(map);
            } else if (dwState == HikConstant.NET_SDK_CONFIG_STATUS_FINISH) {
                log.info("获取卡参数完成");
                break;
            }
        }
        if (list.size() > 0) {
            result.put("code", 1);
            result.put("msg", "获取卡信息成功");
            result.put("data", list);
        }
        if (!hikDevService.NET_DVR_StopRemoteConfig(setCardConfigHandle)) {
            result.put("code", -1);
            result.put("msg", "NET_DVR_StopRemoteConfig接口调用失败，错误码：" + hikDevService.NET_DVR_GetLastError());
        } else {
            log.info("NET_DVR_StopRemoteConfig接口成功");
        }
        return result;
    }

    @Override
    public JSONObject distributeMultiFace(JSONObject jsonObject) {
        JSONObject result = new JSONObject();
        result.put("event", jsonObject.getString("event"));
        //获取用户卡信息列表
        JSONArray userFaceInfoList = jsonObject.getJSONArray("userFaceInfoList");
        //下发多少张脸
        int faceNum = userFaceInfoList.size();
        NET_DVR_FACE_COND struFaceCond = new NET_DVR_FACE_COND();
        struFaceCond.read();
        struFaceCond.dwSize = struFaceCond.size();
        //批量下发，该卡号不需要赋值
        struFaceCond.byCardNo = new byte[32];
        //下发个数
        struFaceCond.dwFaceNum = faceNum;
        //人脸读卡器编号
        struFaceCond.dwEnableReaderNo = 1;
        struFaceCond.write();
        Pointer ptrStruFaceCond = struFaceCond.getPointer();
        Integer longUserId = this.dataCache.getInteger(DataCachePrefixConstant.HIK_REG_USERID_IP + jsonObject.getString("ip"));
        if (null == longUserId) {
            log.error("下发卡失败，longUserId为空");
            result.put("code", -1);
            result.put("msg", "下发卡失败，longUserId为空");
            return result;
        }
        int m_lSetFaceCfgHandle = this.hikDevService.NET_DVR_StartRemoteConfig(longUserId, HikConstant.NET_DVR_SET_FACE, ptrStruFaceCond, struFaceCond.size(), null, null);
        if (m_lSetFaceCfgHandle == -1) {
            System.out.println("建立下发人脸长连接失败，错误码为" + this.hikDevService.NET_DVR_GetLastError());
            result.put("code", -1);
            result.put("msg", "建立下发人脸长连接失败，错误码为" + this.hikDevService.NET_DVR_GetLastError());
            return result;
        } else {
            System.out.println("建立下发人脸长连接成功！");
        }

        NET_DVR_FACE_STATUS struFaceStatus = new NET_DVR_FACE_STATUS();
        struFaceStatus.read();
        struFaceStatus.dwSize = struFaceStatus.size();
        struFaceStatus.write();

        IntByReference pInt = new IntByReference(0);
        for (int i = 0; i < faceNum; i++) {
            //获取用户脸信息对象
            JSONObject userFaceInfo = userFaceInfoList.getJSONObject(i);
            String multiCardNo = userFaceInfo.getString("multiCardNo");
            String base64Pic = userFaceInfo.getString("base64Pic");
            NET_DVR_FACE_RECORD struFaceRecord = new NET_DVR_FACE_RECORD();
            struFaceRecord.read();
            struFaceRecord.dwSize = struFaceRecord.size();

            for (int j = 0; j < HikConstant.ACS_CARD_NO_LEN; j++) {
                struFaceRecord.byCardNo[j] = 0;
            }
            for (int j = 0; j < multiCardNo.length(); j++) {
                struFaceRecord.byCardNo[j] = multiCardNo.getBytes()[j];
            }

            /*****************************************
             * 从本地文件里面读取JPEG图片二进制数据
             *****************************************/
            byte[] decode = Base64.decode(base64Pic);
            //转化为输入流
            ByteArrayInputStream picfile = new ByteArrayInputStream(decode);
            int picdataLength = picfile.available();
            if (picdataLength < 0) {
                System.out.println("input file dataSize < 0");
                result.put("code", -1);
                result.put("msg", "input file dataSize < 0");
                return result;
            }
            BYTE_ARRAY ptrpicByte = new BYTE_ARRAY(picdataLength);
            try {
                picfile.read(ptrpicByte.byValue);
                picfile.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            ptrpicByte.write();
            struFaceRecord.dwFaceLen = picdataLength;
            struFaceRecord.pFaceBuffer = ptrpicByte.getPointer();
            struFaceRecord.write();

            int dwFaceState = this.hikDevService.NET_DVR_SendWithRecvRemoteConfig(m_lSetFaceCfgHandle, struFaceRecord.getPointer(), struFaceRecord.size(), struFaceStatus.getPointer(), struFaceStatus.size(), pInt);
            struFaceStatus.read();
            if (dwFaceState == -1) {
                System.out.println("NET_DVR_SendWithRecvRemoteConfig接口调用失败，错误码：" + this.hikDevService.NET_DVR_GetLastError());
            } else if (dwFaceState == HikConstant.NET_SDK_CONFIG_STATUS_FAILED) {
                System.out.println("下发人脸失败, 卡号: " + new String(struFaceStatus.byCardNo).trim() + ", 错误码：" + this.hikDevService.NET_DVR_GetLastError());
                //可以继续下发下一个
            } else if (dwFaceState == HikConstant.NET_SDK_CONFIG_STATUS_EXCEPTION) {
                result.put("code", -1);
                Map<String,Object> map  = new HashMap<>(1);
                map.put("cardNo",new String(struFaceStatus.byCardNo).trim());
                map.put("msg","下发人脸异常, 卡号: " + new String(struFaceStatus.byCardNo).trim() + ", 错误码：" + this.hikDevService.NET_DVR_GetLastError());
                result.put("msg",map);
                System.out.println("下发人脸异常, 卡号: " + new String(struFaceStatus.byCardNo).trim() + ", 错误码：" + this.hikDevService.NET_DVR_GetLastError());
                return  result;
                //异常是长连接异常，不能继续下发后面的数据，需要重新建立长连接
            } else if (dwFaceState == HikConstant.NET_SDK_CONFIG_STATUS_SUCCESS) {
                if (struFaceStatus.byRecvStatus != 1) {
                    result.put("code", -1);
                    Map<String,Object> map  = new HashMap<>(1);
                    map.put("cardNo",new String(struFaceStatus.byCardNo).trim());
                    map.put("msg","下发人脸失败，人脸读卡器状态" + struFaceStatus.byRecvStatus + ", 卡号：" + new String(struFaceStatus.byCardNo).trim());
                    result.put("msg", map);
                    System.out.println("下发人脸失败，人脸读卡器状态" + struFaceStatus.byRecvStatus + ", 卡号：" + new String(struFaceStatus.byCardNo).trim());
                    return  result;
                } else {
                    System.out.println("下发人脸成功, 卡号: " + new String(struFaceStatus.byCardNo).trim() + ", 状态：" + struFaceStatus.byRecvStatus);
                }
                //可以继续下发下一个
            } else {
                System.out.println("其他状态：" + -1);
            }
        }

        if (!this.hikDevService.NET_DVR_StopRemoteConfig(m_lSetFaceCfgHandle)) {
            System.out.println("NET_DVR_StopRemoteConfig接口调用失败，错误码：" + this.hikDevService.NET_DVR_GetLastError());
            result.put("code", -1);
            result.put("msg", "NET_DVR_StopRemoteConfig接口调用失败，错误码：" + this.hikDevService.NET_DVR_GetLastError());
            return result;
        } else {
            System.out.println("NET_DVR_StopRemoteConfig接口成功");
        }
        result.put("code", 0);
        result.put("msg", "NET_DVR_StopRemoteConfig接口成功");
        return result;
    }

    @Override
    public JSONObject distributeMultiCard(JSONObject jsonObject) {
        JSONObject result = new JSONObject();
        result.put("event", jsonObject.getString("event"));
        //获取用户卡信息列表
        JSONArray userCardInfoList = jsonObject.getJSONArray("userCardInfoList");
        //下发多少张卡
        int cardNum = userCardInfoList.size();
        NET_DVR_CARD_COND struCardCond = new NET_DVR_CARD_COND();
        struCardCond.read();
        struCardCond.dwSize = struCardCond.size();
        //下发张数
        struCardCond.dwCardNum = cardNum;
        struCardCond.write();
        Pointer ptrStrCond = struCardCond.getPointer();
        Integer longUserId = this.dataCache.getInteger(DataCachePrefixConstant.HIK_REG_USERID_IP + jsonObject.getString("ip"));
        if (null == longUserId) {
            log.error("下发卡失败，longUserId为空");
            result.put("code", -1);
            result.put("msg", "下发卡失败，longUserId为空");
            return result;
        }
        int setCardConfigHandle = this.hikDevService.NET_DVR_StartRemoteConfig(longUserId, HikConstant.NET_DVR_SET_CARD, ptrStrCond, struCardCond.size(), null, null);
        if (setCardConfigHandle == -1) {
            log.error("建立下发卡长连接失败，错误码为" + this.hikDevService.NET_DVR_GetLastError());
            result.put("code", -1);
            result.put("msg", "建立下发卡长连接失败，错误码为" + this.hikDevService.NET_DVR_GetLastError());
            return result;
        } else {
            log.info("建立下发卡长连接成功！");
        }

        NET_DVR_CARD_RECORD[] struCardRecord = (NET_DVR_CARD_RECORD[]) new NET_DVR_CARD_RECORD().toArray(cardNum);

        NET_DVR_CARD_STATUS strCardStatus = new NET_DVR_CARD_STATUS();
        strCardStatus.read();
        strCardStatus.dwSize = strCardStatus.size();
        strCardStatus.write();

        IntByReference pInt = new IntByReference(0);

        for (int i = 0; i < cardNum; i++) {
            //获取用户卡信息对象
            JSONObject userCardInfo = userCardInfoList.getJSONObject(i);
            String cardNo = userCardInfo.getString("cardNo");
            Integer employeeNo = userCardInfo.getInteger("employeeNo");
            String cardName = userCardInfo.getString("cardName");
            struCardRecord[i].read();
            struCardRecord[i].dwSize = struCardRecord[i].size();

            for (int j = 0; j < HikConstant.ACS_CARD_NO_LEN; j++) {
                struCardRecord[i].byCardNo[j] = 0;
            }
            System.arraycopy(cardNo.getBytes(), 0, struCardRecord[i].byCardNo, 0, cardNo.getBytes().length);

            //普通卡
            struCardRecord[i].byCardType = 1;
            //是否为首卡，0-否，1-是
            struCardRecord[i].byLeaderCard = 0;
            struCardRecord[i].byUserType = 0;
            //门1有权限
            struCardRecord[i].byDoorRight[0] = 1;
            // TODO 关联门计划模板，使用了前面配置的计划模板
//            struCardRecord[i].wCardRightPlan[0] = wPlanTemplateNumber;

            //卡有效期使能，下面是卡有效期从2000-1-1 11:11:11到2030-1-1 11:11:11
            struCardRecord[i].struValid.byEnable = 1;
            struCardRecord[i].struValid.struBeginTime.wYear = 2000;
            struCardRecord[i].struValid.struBeginTime.byMonth = 1;
            struCardRecord[i].struValid.struBeginTime.byDay = 1;
            struCardRecord[i].struValid.struBeginTime.byHour = 11;
            struCardRecord[i].struValid.struBeginTime.byMinute = 11;
            struCardRecord[i].struValid.struBeginTime.bySecond = 11;
            struCardRecord[i].struValid.struEndTime.wYear = 2030;
            struCardRecord[i].struValid.struEndTime.byMonth = 1;
            struCardRecord[i].struValid.struEndTime.byDay = 1;
            struCardRecord[i].struValid.struEndTime.byHour = 11;
            struCardRecord[i].struValid.struEndTime.byMinute = 11;
            struCardRecord[i].struValid.struEndTime.bySecond = 11;
            //工号
            struCardRecord[i].dwEmployeeNo = employeeNo;
            //姓名
            byte[] strCardName = cardName.getBytes(StandardCharsets.UTF_8);
            for (int j = 0; j < HikConstant.NAME_LEN; j++) {
                struCardRecord[i].byName[j] = 0;
            }
            System.arraycopy(strCardName, 0, struCardRecord[i].byName, 0, strCardName.length);
            struCardRecord[i].write();
            int dwState = this.hikDevService.NET_DVR_SendWithRecvRemoteConfig(setCardConfigHandle, struCardRecord[i].getPointer(), struCardRecord[i].size(), strCardStatus.getPointer(), strCardStatus.size(), pInt);
            strCardStatus.read();
            if (dwState == -1) {
                log.error("NET_DVR_SendWithRecvRemoteConfig接口调用失败，错误码：" + this.hikDevService.NET_DVR_GetLastError());
                result.put("code", -1);
                result.put("msg", "NET_DVR_SendWithRecvRemoteConfig接口调用失败，错误码：" + this.hikDevService.NET_DVR_GetLastError());
                return result;
            } else if (dwState == HikConstant.NET_SDK_CONFIG_STATUS_FAILED) {
                log.error("下发卡失败, 卡号: " + new String(strCardStatus.byCardNo).trim() + ", 错误码：" + strCardStatus.dwErrorCode);
                result.put("code", -1);
                result.put("msg", "下发卡失败, 卡号: " + new String(strCardStatus.byCardNo).trim() + ", 错误码：" + strCardStatus.dwErrorCode);
                return result;
                //可以继续下发下一个
            } else if (dwState == HikConstant.NET_SDK_CONFIG_STATUS_EXCEPTION) {
                log.error("下发卡异常, 卡号: " + new String(strCardStatus.byCardNo).trim() + ", 错误码：" + strCardStatus.dwErrorCode);
                result.put("code", -1);
                result.put("msg", "下发卡异常, 卡号: " + new String(strCardStatus.byCardNo).trim() + ", 错误码：" + strCardStatus.dwErrorCode);
                return result;
                //异常是长连接异常，不能继续下发后面的数据，需要重新建立长连接
            } else if (dwState == HikConstant.NET_SDK_CONFIG_STATUS_SUCCESS) {
                if (strCardStatus.dwErrorCode != 0) {
                    log.error("下发卡失败,错误码" + strCardStatus.dwErrorCode + ", 卡号：" + new String(strCardStatus.byCardNo).trim());
                    result.put("code", -1);
                    result.put("msg", "下发卡失败,错误码" + strCardStatus.dwErrorCode + ", 卡号：" + new String(strCardStatus.byCardNo).trim());
                    return result;
                } else {
                    log.info("下发卡成功, 卡号: " + new String(strCardStatus.byCardNo).trim() + ", 状态：" + strCardStatus.byStatus);
                }
                //可以继续下发下一个
            } else {
                log.info("其他状态：" + dwState);
            }
        }

        if (!this.hikDevService.NET_DVR_StopRemoteConfig(setCardConfigHandle)) {
            log.error("NET_DVR_StopRemoteConfig接口调用失败，错误码：" + this.hikDevService.NET_DVR_GetLastError());
            result.put("code", -1);
            result.put("msg", "NET_DVR_StopRemoteConfig接口调用失败，错误码：" + this.hikDevService.NET_DVR_GetLastError());
            return result;
        } else {
            log.info("NET_DVR_StopRemoteConfig接口成功");
            result.put("code", 0);
            result.put("msg", "NET_DVR_StopRemoteConfig接口成功");
            return result;
        }
    }

    @Override
    public JSONObject deleteCardByCardNo(JSONObject jsonObject) {
        JSONObject result = new JSONObject();
        result.put("event", jsonObject.getString("event"));
        String strCardNo = jsonObject.getString("cardNo");
        NET_DVR_CARD_COND struCardCond = new NET_DVR_CARD_COND();
        struCardCond.read();
        struCardCond.dwSize = struCardCond.size();
        struCardCond.dwCardNum = 1;  //下发一张
        struCardCond.write();
        Pointer ptrStruCond = struCardCond.getPointer();
        Integer longUserId = this.dataCache.getInteger(DataCachePrefixConstant.HIK_REG_USERID_IP + jsonObject.getString("ip"));
        if (null == longUserId) {
            log.error("下发卡失败，longUserId为空");
            result.put("code", -1);
            result.put("msg", "下发卡失败，longUserId为空");
            return result;
        }
        int m_lSetCardCfgHandle = this.hikDevService.NET_DVR_StartRemoteConfig(longUserId, HikConstant.NET_DVR_DEL_CARD, ptrStruCond, struCardCond.size(), null, null);
        if (m_lSetCardCfgHandle == -1) {
            System.out.println("建立删除卡长连接失败，错误码为" + this.hikDevService.NET_DVR_GetLastError());
            result.put("code", -1);
            result.put("msg", "建立删除卡长连接失败，错误码为" + this.hikDevService.NET_DVR_GetLastError());
            return result;
        } else {
            System.out.println("建立删除卡长连接成功！");
        }

        NET_DVR_CARD_SEND_DATA struCardData = new NET_DVR_CARD_SEND_DATA();
        struCardData.read();
        struCardData.dwSize = struCardData.size();

        for (int i = 0; i < HikConstant.ACS_CARD_NO_LEN; i++) {
            struCardData.byCardNo[i] = 0;
        }
        for (int i = 0; i < strCardNo.length(); i++) {
            struCardData.byCardNo[i] = strCardNo.getBytes()[i];
        }
        struCardData.write();

        NET_DVR_CARD_STATUS struCardStatus = new NET_DVR_CARD_STATUS();
        struCardStatus.read();
        struCardStatus.dwSize = struCardStatus.size();
        struCardStatus.write();

        IntByReference pInt = new IntByReference(0);

        while (true) {
            int dwState = this.hikDevService.NET_DVR_SendWithRecvRemoteConfig(m_lSetCardCfgHandle, struCardData.getPointer(), struCardData.size(), struCardStatus.getPointer(), struCardStatus.size(), pInt);
            struCardStatus.read();
            if (dwState == -1) {
                System.out.println("NET_DVR_SendWithRecvRemoteConfig接口调用失败，错误码：" + this.hikDevService.NET_DVR_GetLastError());
                result.put("code", -1);
                result.put("msg", "NET_DVR_SendWithRecvRemoteConfig接口调用失败，错误码：" + this.hikDevService.NET_DVR_GetLastError());
                return result;
            } else if (dwState == HikConstant.NET_SDK_CONFIG_STATUS_NEEDWAIT) {
                System.out.println("配置等待");
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                continue;
            } else if (dwState == HikConstant.NET_SDK_CONFIG_STATUS_FAILED) {
                System.out.println("删除卡失败, 卡号: " + new String(struCardStatus.byCardNo).trim() + ", 错误码：" + struCardStatus.dwErrorCode);
                result.put("code", -1);
                result.put("msg", "删除卡失败, 卡号: " + new String(struCardStatus.byCardNo).trim() + ", 错误码：" + struCardStatus.dwErrorCode);
                return result;
            } else if (dwState == HikConstant.NET_SDK_CONFIG_STATUS_EXCEPTION) {
                System.out.println("删除卡异常, 卡号: " + new String(struCardStatus.byCardNo).trim() + ", 错误码：" + struCardStatus.dwErrorCode);
                result.put("code", -1);
                result.put("msg", "删除卡异常, 卡号: " + new String(struCardStatus.byCardNo).trim() + ", 错误码：" + struCardStatus.dwErrorCode);
                return result;
            } else if (dwState == HikConstant.NET_SDK_CONFIG_STATUS_SUCCESS) {
                if (struCardStatus.dwErrorCode != 0) {
                    System.out.println("删除卡成功,但是错误码" + struCardStatus.dwErrorCode + ", 卡号：" + new String(struCardStatus.byCardNo).trim());
                } else {
                    System.out.println("删除卡成功, 卡号: " + new String(struCardStatus.byCardNo).trim() + ", 状态：" + struCardStatus.byStatus);
                }
                continue;
            } else if (dwState == HikConstant.NET_SDK_CONFIG_STATUS_FINISH) {
                System.out.println("删除卡完成");
                break;
            }
        }

        if (!this.hikDevService.NET_DVR_StopRemoteConfig(m_lSetCardCfgHandle)) {
            System.out.println("NET_DVR_StopRemoteConfig接口调用失败，错误码：" + this.hikDevService.NET_DVR_GetLastError());
            result.put("code", -1);
            result.put("msg", "NET_DVR_StopRemoteConfig接口调用失败，错误码：" + this.hikDevService.NET_DVR_GetLastError());
        } else {
            System.out.println("NET_DVR_StopRemoteConfig接口成功");
            result.put("code", 0);
            result.put("msg", "NET_DVR_StopRemoteConfig接口成功");
        }
        return result;
    }

    @Override
    public JSONObject deleteFaceByCardNo(JSONObject jsonObject) {
        JSONObject result = new JSONObject();
        result.put("event", jsonObject.getString("event"));
        String strCardNo = jsonObject.getString("cardNo");
        NET_DVR_FACE_PARAM_CTRL struFaceDelCond = new NET_DVR_FACE_PARAM_CTRL();
        struFaceDelCond.dwSize = struFaceDelCond.size();
        //删除方式：0- 按卡号方式删除，1- 按读卡器删除
        struFaceDelCond.byMode = 0;

        struFaceDelCond.struProcessMode.setType(NET_DVR_FACE_PARAM_BYCARD.class);

        //需要删除人脸关联的卡号
        for (int i = 0; i < HikConstant.ACS_CARD_NO_LEN; i++) {
            struFaceDelCond.struProcessMode.struByCard.byCardNo[i] = 0;
        }
        System.arraycopy(strCardNo.getBytes(), 0, struFaceDelCond.struProcessMode.struByCard.byCardNo, 0, strCardNo.length());
        //读卡器
        struFaceDelCond.struProcessMode.struByCard.byEnableCardReader[0] = 1;
        //人脸ID
        struFaceDelCond.struProcessMode.struByCard.byFaceID[0] = 1;
        struFaceDelCond.write();

        Pointer ptrFaceDelCond = struFaceDelCond.getPointer();
        Integer longUserId = this.dataCache.getInteger(DataCachePrefixConstant.HIK_REG_USERID_IP + jsonObject.getString("ip"));
        if (null == longUserId) {
            log.error("下发卡失败，longUserId为空");
            result.put("code", -1);
            result.put("msg", "下发卡失败，longUserId为空");
            return result;
        }
        boolean bRet = this.hikDevService.NET_DVR_RemoteControl(longUserId, HikConstant.NET_DVR_DEL_FACE_PARAM_CFG, ptrFaceDelCond, struFaceDelCond.size());
        if (!bRet) {
            System.out.println("删除人脸失败，错误码为" + this.hikDevService.NET_DVR_GetLastError());
            result.put("code", -1);
            result.put("msg", "删除人脸失败，错误码为" + this.hikDevService.NET_DVR_GetLastError());
        } else {
            System.out.println("删除人脸成功！");
            result.put("code", 0);
            result.put("msg", "删除人脸成功！");
        }
        return result;
    }
}
