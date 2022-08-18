package com.oldwei.hikdev.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.oldwei.hikdev.constant.HikConstant;
import com.oldwei.hikdev.entity.HikDevResponse;
import com.oldwei.hikdev.entity.QueryRequest;
import com.oldwei.hikdev.entity.access.AccessPeople;
import com.oldwei.hikdev.entity.access.RightPlan;
import com.oldwei.hikdev.entity.access.UserInfoSearch;
import com.oldwei.hikdev.entity.access.Valid;
import com.oldwei.hikdev.entity.param.AccessControlUser;
import com.oldwei.hikdev.service.IHikAccessControlService;
import com.oldwei.hikdev.service.IHikDevService;
import com.oldwei.hikdev.structure.*;
import com.oldwei.hikdev.util.ConfigJsonUtil;
import com.oldwei.hikdev.util.StringEncodingUtil;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author oldwei
 * @date 2021-9-27 11:50
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HikAccessControlServiceImpl implements IHikAccessControlService {
    private final IHikDevService hikDevService;

    @Override
    public HikDevResponse getAllCardInfo(String ip) {
        HikDevResponse result = new HikDevResponse();
        if (StrUtil.isBlank(ip)) {
            result.err("缺少必要参数字段：ip");
            return result;
        }
        Integer longUserId = ConfigJsonUtil.getDeviceSearchInfoByIp(ip).getLoginId();
        if (longUserId < 0) {
            return result.err("设备未注册");
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
            result.err("建立下发卡长连接失败，错误码为" + hikDevService.NET_DVR_GetLastError());
            return result;
        }

        NET_DVR_CARD_RECORD struCardRecord = new NET_DVR_CARD_RECORD();
        struCardRecord.read();
        struCardRecord.dwSize = struCardRecord.size();
        struCardRecord.write();

        IntByReference pInt = new IntByReference(0);
        int iCharEncodeType = Integer.parseInt(ConfigJsonUtil.getDeviceSearchInfoByIp(ip).getCharEncodeType());
        List<Map<String, Object>> list = new ArrayList<>();
        while (true) {
            //下发卡数据状态
            int dwState = hikDevService.NET_DVR_GetNextRemoteConfig(setCardConfigHandle, struCardRecord.getPointer(), struCardRecord.size());
            struCardRecord.read();
            if (dwState == -1) {
                result.err("NET_DVR_SendWithRecvRemoteConfig接口调用失败，错误码：" + hikDevService.NET_DVR_GetLastError());
                break;
            } else if (dwState == HikConstant.NET_SDK_CONFIG_STATUS_NEEDWAIT) {
                log.info("配置等待");
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (dwState == HikConstant.NET_SDK_CONFIG_STATUS_FAILED) {
                result.err("获取卡参数失败");
                break;
            } else if (dwState == HikConstant.NET_SDK_CONFIG_STATUS_EXCEPTION) {
                result.err("获取卡参数异常");
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
                break;
            }
        }
        if (list.size() > 0) {
            result.ok("获取卡信息成功", list);
        }
        if (!hikDevService.NET_DVR_StopRemoteConfig(setCardConfigHandle)) {
            result.err("NET_DVR_StopRemoteConfig接口调用失败，错误码：" + hikDevService.NET_DVR_GetLastError());
        }
        return result;
    }

    @Override
    public HikDevResponse getAllUserInfo(String ip, String[] employeeNos, QueryRequest queryRequest) {
        String urlInBuffer = "POST /ISAPI/AccessControl/UserInfo/Search?format=json";
        //=====================================================================
        //组装查询的JSON报文，这边查询的是所有的卡
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonSearchCond = new JSONObject();

        //如果需要查询指定的工号人员信息 EmployeeNoList可选，人员 ID 列表
        if (employeeNos.length > 0) {
            jsonSearchCond.put("EmployeeNoList", packageUserNos(employeeNos));
        }
        //必填，string，搜索记录唯一标识，用来确认上层客户端是否为同一个（倘若是同一个,设备记录内存,下次搜索加快速度）暂时写死吧，反正同一个查询还快
        jsonSearchCond.put("searchID", "123e4567-e89b-12d3-a456-426655440000");
        // 必填，integer，查询结果在结果列表中的起始位置。当记录条数很多时，一次查询不能获取所有的记录，下一次查询时指定位置可以查询后面的记录
        // （若设备支持的最大 totalMatches 为 M 个，但是当前设备已存储的 totalMatches 为 N 个（N<=M），则该字段的合法范围为 0~N-1）
        jsonSearchCond.put("searchResultPosition", 10 * (queryRequest.getPageNum() - 1));
        //必填，integer，本次协议调用可获取的最大记录数（如maxResults 值大于设备能力集返回的范围，则设备按照能力集最大值返回，设备不进行报错）
        jsonSearchCond.put("maxResults", queryRequest.getPageSize());
        jsonObject.put("UserInfoSearchCond", jsonSearchCond);

        String strInBuff = jsonObject.toJSONString();
        //=====================================================================
        BYTE_ARRAY ptrInBuff = new BYTE_ARRAY(strInBuff.length());
        ptrInBuff.byValue = strInBuff.getBytes(StandardCharsets.UTF_8);
        ptrInBuff.write();
        JSONObject result = this.userInfo(ip, urlInBuffer, ptrInBuff.getPointer(), strInBuff.length(), 2550, 1024 * 10);

        //因为name是字节数组，所以需要自己转一下
        UserInfoSearch userInfoSearch = result.getJSONObject("data").getJSONObject("UserInfoSearch").to(UserInfoSearch.class);
//        log.info("{}", userInfoSearch);
//        if (null != userInfoSearch.getUserInfo() && userInfoSearch.getUserInfo().size() > 0) {
//            userInfoSearch.getUserInfo().forEach(people -> {
//                try {
//                    //这里必须加上双引号反序列化，因为在序列化的时候是json带双引号
//                    String nameByte = "\"" + people.getRealName() + "\"";
//                    //如果realName是json序列化的字符串，那么需要反序列化
//                    byte[] bytes = JSON.parseObject(nameByte, byte[].class);
//                    String realName = new String(bytes).trim();
//                    people.setRealName(realName);
//                } catch (ArrayIndexOutOfBoundsException ignored) {
//                    // 汉字是越界异常超出256，不加上引号是expect '[', but error, pos 1, line 1, column 2
//                    // 已经是汉字的名字，也就是说他们的名字是从设备上直接录制的，并没有走sdk，
//                    // 这时候就会反序列化错误，那么我们就直接忽略这些错误，把realName原样输出
//                }
//
//            });
//        }
        userInfoSearch.setPageNum(queryRequest.getPageNum());
        return new HikDevResponse().ok(result.getString("msg"), userInfoSearch);
    }

    private List<Map<String, Object>> packageUserNos(String[] employeeNos) {
        List<Map<String, Object>> employeeNoList = new ArrayList<>();
        for (String no : employeeNos) {
            Map<String, Object> employeeNo = new HashMap<>(1);
            //employeeNo可选，string，人员 ID
            employeeNo.put("employeeNo", no);
            employeeNoList.add(employeeNo);
        }
        return employeeNoList;
    }

    @Override
    public HikDevResponse addUser(String ip, AccessControlUser accessControlUser) {
        String urlInBuffer = "POST /ISAPI/AccessControl/UserInfo/Record?format=json";
        AccessPeople accessPeople = new AccessPeople();
        accessPeople.setRealName(accessControlUser.getRealName());
        accessPeople.setEmployeeNo(accessControlUser.getEmployeeNo());
        JSONObject result = this.aboutUserInfo(ip, accessPeople, urlInBuffer);
        return new HikDevResponse().ok(result.getString("msg"));
    }

    @Override
    public HikDevResponse modifyUser(AccessPeople accessPeople) {
        String urlInBuffer = "PUT /ISAPI/AccessControl/UserInfo/Modify?format=json";
        JSONObject result = this.aboutUserInfo(accessPeople.getIpv4Address(), accessPeople, urlInBuffer);
        return new HikDevResponse().ok(result.getString("msg"));
    }

    @Override
    public HikDevResponse addMultiUser(String ip, List<AccessControlUser> accessControlUserList) {
        for (AccessControlUser user : accessControlUserList) {
            this.addUser(ip, user);
        }
        return new HikDevResponse().ok("批量下发用户完成");
    }

    @Override
    public HikDevResponse addUserFace(String ip, AccessControlUser accessControlUser) {
        String urlInBuffer = "POST /ISAPI/Intelligent/FDLib/FaceDataRecord?format=json";
        HikDevResponse result = new HikDevResponse();
        Integer lHandler = this.startRemoteConfig(ip, urlInBuffer, 2551);
        if (lHandler < 0) {
            return result.err("NET_DVR_StartRemoteConfig 接口调用失败，错误码：" + this.hikDevService.NET_DVR_GetLastError());
        }
        NET_DVR_JSON_DATA_CFG strAddFaceDataCfg = new NET_DVR_JSON_DATA_CFG();
        strAddFaceDataCfg.read();
        strAddFaceDataCfg.dwSize = strAddFaceDataCfg.size();
        //字符串拷贝到数组中
        BYTE_ARRAY ptrByteArray = new BYTE_ARRAY(1024);

        Integer employeeNo = accessControlUser.getEmployeeNo();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("faceLibType", "blackFD");
        jsonObject.put("FDID", "1");
        //人脸下发关联的工号
        jsonObject.put("FPID", employeeNo);
        String strJsonData = jsonObject.toString();
        System.out.println("下发人脸的json报文:" + strJsonData);
        System.arraycopy(strJsonData.getBytes(), 0, ptrByteArray.byValue, 0, strJsonData.length());
        ptrByteArray.write();
        strAddFaceDataCfg.lpJsonData = ptrByteArray.getPointer();
        strAddFaceDataCfg.dwJsonDataSize = strJsonData.length();

        /*****************************************
         * 从本地文件里面读取JPEG图片二进制数据
         *****************************************/
        //下发的人脸图片
        String base64Pic = accessControlUser.getBase64Pic();
        byte[] decode = Base64.decode(base64Pic);
        //转化为输入流
        ByteArrayInputStream picfile = new ByteArrayInputStream(decode);
        int picdataLength = picfile.available();
        if (picdataLength < 0) {
            System.out.println("input file dataSize < 0");
        }
        BYTE_ARRAY ptrpicByte = new BYTE_ARRAY(picdataLength);
        try {
            picfile.read(ptrpicByte.byValue);
            picfile.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        ptrpicByte.write();
        strAddFaceDataCfg.dwPicDataSize = picdataLength;
        strAddFaceDataCfg.lpPicData = ptrpicByte.getPointer();
        strAddFaceDataCfg.write();

        BYTE_ARRAY ptrOutBuff = new BYTE_ARRAY(1024);
        IntByReference pInt = new IntByReference(0);
        int dwState = this.hikDevService.NET_DVR_SendWithRecvRemoteConfig(lHandler,
                strAddFaceDataCfg.getPointer(), strAddFaceDataCfg.dwSize,
                ptrOutBuff.getPointer(), ptrOutBuff.size(), pInt);
        //读取返回的json并解析
        ptrOutBuff.read();
        String strResult = new String(ptrOutBuff.byValue).trim();
        JSONObject jsonResult = JSONObject.parseObject(strResult);
        result.put("data", jsonResult);
        if (dwState != HikConstant.NET_SDK_CONFIG_STATUS_SUCCESS) {
            //返回NET_SDK_CONFIG_STATUS_SUCCESS代表流程走通了，但并不代表下发成功，比如有些设备可能因为人员已存在等原因下发失败，所以需要解析Json报文 如果statusCode=1无异常情况,否则就是有异常情况
            result.err("NET_DVR_SendWithRecvRemoteConfig接口调用失败，错误码：" + this.hikDevService.NET_DVR_GetLastError());
        }
        // TODO 下发人员时：dwState其实不会走到这里，因为设备不知道我们会下发多少个人，所以长连接需要我们主动关闭
        if (!this.hikDevService.NET_DVR_StopRemoteConfig(lHandler)) {
            result.err("NET_DVR_StopRemoteConfig接口调用失败，错误码：" + this.hikDevService.NET_DVR_GetLastError());
        }
        return result;
    }

    @Override
    public HikDevResponse addMultiUserFace(String ip, List<AccessControlUser> accessControlUserList) {
        for (AccessControlUser user : accessControlUserList) {
            this.addUserFace(ip, user);
        }
        return new HikDevResponse().ok("批量下发人脸完成");
    }

    @Override
    public HikDevResponse delMultiUserFace(String ip, String[] employeeIds) {
        String urlInBuffer = "PUT /ISAPI/Intelligent/FDLib/FDSearch/Delete?format=json&FDID=1&faceLibType=blackFD";
        JSONArray array = new JSONArray();
        //================
        for (String employeeId : employeeIds) {
            JSONObject valueObj = new JSONObject();
            valueObj.put("value", employeeId);
            array.add(valueObj);
        }
        JSONObject fpid = new JSONObject();
        fpid.put("FPID", array);
        String strInBuffer = fpid.toJSONString();
        //=============
        return deleteOperation(ip, urlInBuffer, strInBuffer);
    }

    @Override
    public HikDevResponse delMultiUser(String ip, String[] employeeIds) {
        String urlInBuffer = "PUT /ISAPI/AccessControl/UserInfo/Delete?format=json";
        JSONArray array = new JSONArray();
        //=====================
        for (String employeeId : employeeIds) {
            JSONObject employeeNo = new JSONObject();
            employeeNo.put("employeeNo", employeeId);
            array.add(employeeNo);
        }
        JSONObject employeeNoList = new JSONObject();
        employeeNoList.put("EmployeeNoList", array);
        JSONObject userInfoDelCond = new JSONObject();
        userInfoDelCond.put("UserInfoDelCond", employeeNoList);
        String strInBuffer = userInfoDelCond.toJSONString();
        //======================
        return deleteOperation(ip, urlInBuffer, strInBuffer);
    }

    @Override
    public HikDevResponse addMultiCard(String ip, List<AccessControlUser> accessControlUserList) {
        HikDevResponse result = new HikDevResponse();
        //下发多少张卡
        int cardNum = accessControlUserList.size();
        NET_DVR_CARD_COND struCardCond = new NET_DVR_CARD_COND();
        struCardCond.read();
        struCardCond.dwSize = struCardCond.size();
        //下发张数
        struCardCond.dwCardNum = cardNum;
        struCardCond.write();
        Pointer ptrStrCond = struCardCond.getPointer();
        Integer longUserId = ConfigJsonUtil.getDeviceSearchInfoByIp(ip).getLoginId();
        int setCardConfigHandle = this.hikDevService.NET_DVR_StartRemoteConfig(longUserId, HikConstant.NET_DVR_SET_CARD, ptrStrCond, struCardCond.size(), null, null);
        if (setCardConfigHandle == -1) {
            return result.err("建立下发卡长连接失败，错误码为" + this.hikDevService.NET_DVR_GetLastError());
        }

        NET_DVR_CARD_RECORD[] struCardRecord = (NET_DVR_CARD_RECORD[]) new NET_DVR_CARD_RECORD().toArray(cardNum);

        NET_DVR_CARD_STATUS strCardStatus = new NET_DVR_CARD_STATUS();
        strCardStatus.read();
        strCardStatus.dwSize = strCardStatus.size();
        strCardStatus.write();

        IntByReference pInt = new IntByReference(0);

        for (int i = 0; i < cardNum; i++) {
            //获取用户卡信息对象
            AccessControlUser userCardInfo = accessControlUserList.get(i);
            String cardNo = userCardInfo.getCardNo();
            Integer employeeNo = userCardInfo.getEmployeeNo();
            String cardName = userCardInfo.getRealName();
            Short wPlanTemplateNumber = userCardInfo.getPlanTemplateNumber();
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
            struCardRecord[i].wCardRightPlan[0] = wPlanTemplateNumber;

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
                return result.err("NET_DVR_SendWithRecvRemoteConfig接口调用失败，错误码：" + this.hikDevService.NET_DVR_GetLastError());
            } else if (dwState == HikConstant.NET_SDK_CONFIG_STATUS_FAILED) {
                return result.err("下发卡失败, 卡号: " + new String(strCardStatus.byCardNo).trim() + ", 错误码：" + strCardStatus.dwErrorCode);
                //可以继续下发下一个
            } else if (dwState == HikConstant.NET_SDK_CONFIG_STATUS_EXCEPTION) {
                return result.err("下发卡异常, 卡号: " + new String(strCardStatus.byCardNo).trim() + ", 错误码：" + strCardStatus.dwErrorCode);
                //异常是长连接异常，不能继续下发后面的数据，需要重新建立长连接
            } else if (dwState == HikConstant.NET_SDK_CONFIG_STATUS_SUCCESS) {
                if (strCardStatus.dwErrorCode != 0) {
                    return result.err("下发卡失败,错误码" + strCardStatus.dwErrorCode + ", 卡号：" + new String(strCardStatus.byCardNo).trim());
                } else {
                    log.info("下发卡成功, 卡号: " + new String(strCardStatus.byCardNo).trim() + ", 状态：" + strCardStatus.byStatus);
                }
                //可以继续下发下一个
            } else {
                log.info("其他状态：" + dwState);
            }
        }

        if (!this.hikDevService.NET_DVR_StopRemoteConfig(setCardConfigHandle)) {
            return result.err("msg", "NET_DVR_StopRemoteConfig接口调用失败，错误码：" + this.hikDevService.NET_DVR_GetLastError());
        } else {
            return result.ok("NET_DVR_StopRemoteConfig接口成功");
        }
    }

    @Override
    public HikDevResponse addMultiCardFace(String ip, List<AccessControlUser> accessControlUserList) {
        HikDevResponse result = new HikDevResponse();
        //下发多少张脸
        int faceNum = accessControlUserList.size();
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
        Integer longUserId = ConfigJsonUtil.getDeviceSearchInfoByIp(ip).getLoginId();
        int m_lSetFaceCfgHandle = this.hikDevService.NET_DVR_StartRemoteConfig(longUserId, HikConstant.NET_DVR_SET_FACE, ptrStruFaceCond, struFaceCond.size(), null, null);
        if (m_lSetFaceCfgHandle == -1) {
            return result.err("建立下发人脸长连接失败，错误码为" + this.hikDevService.NET_DVR_GetLastError());
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
            AccessControlUser userFaceInfo = accessControlUserList.get(i);
            String multiCardNo = userFaceInfo.getCardNo();
            String base64Pic = userFaceInfo.getBase64Pic();
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
                return result.err("input file dataSize < 0");
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
                log.info("NET_DVR_SendWithRecvRemoteConfig接口调用失败，错误码：" + this.hikDevService.NET_DVR_GetLastError());
            } else if (dwFaceState == HikConstant.NET_SDK_CONFIG_STATUS_FAILED) {
                log.info("下发人脸失败, 卡号: " + new String(struFaceStatus.byCardNo).trim() + ", 错误码：" + this.hikDevService.NET_DVR_GetLastError());
                //可以继续下发下一个
            } else if (dwFaceState == HikConstant.NET_SDK_CONFIG_STATUS_EXCEPTION) {
                log.info("下发人脸异常, 卡号: " + new String(struFaceStatus.byCardNo).trim() + ", 错误码：" + this.hikDevService.NET_DVR_GetLastError());
                return result.err("下发人脸异常, 卡号: " + new String(struFaceStatus.byCardNo).trim() + ", 错误码：" + this.hikDevService.NET_DVR_GetLastError());
                //异常是长连接异常，不能继续下发后面的数据，需要重新建立长连接
            } else if (dwFaceState == HikConstant.NET_SDK_CONFIG_STATUS_SUCCESS) {
                if (struFaceStatus.byRecvStatus != 1) {
                    // 人脸读卡器状态，按字节表示，0-失败，1-成功，2-重试或人脸质量差，
                    // 3-内存已满(人脸数据满)，4-已存在该人脸，5-非法人脸 ID，6-算法建模失败，7-未下发卡权限，
                    // 8-未定义（保留），9-人眼间距小距小，10-图片数据长度小于 1KB，11-图片格式不符（png/jpg/bmp），
                    // 12-图片像素数量超过上限，13-图片像素数量低于下限，14-图片信息校验失败，15-图片解码失败，
                    // 16-人脸检测失败，17-人脸评分失败
                    log.info("下发人脸失败，人脸读卡器状态" + struFaceStatus.byRecvStatus + ", 卡号：" + new String(struFaceStatus.byCardNo).trim());
                    return result.err("下发人脸失败，人脸读卡器状态" + struFaceStatus.byRecvStatus + ", 卡号：" + new String(struFaceStatus.byCardNo).trim());
                } else {
                    log.info("下发人脸成功, 卡号: " + new String(struFaceStatus.byCardNo).trim() + ", 状态：" + struFaceStatus.byRecvStatus);
                }
                //可以继续下发下一个
            } else {
                log.info("其他状态：" + -1);
            }
        }

        if (!this.hikDevService.NET_DVR_StopRemoteConfig(m_lSetFaceCfgHandle)) {
            return result.err("NET_DVR_StopRemoteConfig接口调用失败，错误码：" + this.hikDevService.NET_DVR_GetLastError());
        }
        return result.ok("NET_DVR_StopRemoteConfig接口成功");
    }

    @Override
    public HikDevResponse delMultiCardFace(String ip, String[] cardNoIds) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("以下卡号操作失败：");
        for (String cardNo :
                cardNoIds) {
            boolean b = this.delCardFace(ip, cardNo);
            if (!b) {
                stringBuilder.append(cardNo).append(";");
            }
        }
        return new HikDevResponse().ok(stringBuilder.toString());
    }

    @Override
    public HikDevResponse delMultiCard(String ip, String[] cardNoIds) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("以下卡号操作失败：");
        for (String cardNo :
                cardNoIds) {
            boolean b = this.delCard(ip, cardNo);
            if (!b) {
                stringBuilder.append(cardNo).append(";");
            }
        }
        return new HikDevResponse().ok(stringBuilder.toString());
    }

    @Override
    public HikDevResponse setCartTemplate(String ip, Integer planTemplateNumber) {
        int iErr = 0;
        HikDevResponse result = new HikDevResponse();

        //设置卡权限计划模板参数
        NET_DVR_PLAN_TEMPLATE_COND struPlanCond = new NET_DVR_PLAN_TEMPLATE_COND();
        struPlanCond.dwSize = struPlanCond.size();
        struPlanCond.dwPlanTemplateNumber = planTemplateNumber;//计划模板编号，从1开始，最大值从门禁能力集获取
        struPlanCond.wLocalControllerID = 0;//就地控制器序号[1,64]，0表示门禁主机
        struPlanCond.write();

        NET_DVR_PLAN_TEMPLATE struPlanTemCfg = new NET_DVR_PLAN_TEMPLATE();
        struPlanTemCfg.dwSize = struPlanTemCfg.size();
        struPlanTemCfg.byEnable = 1; //是否使能：0- 否，1- 是
        struPlanTemCfg.dwWeekPlanNo = 1;//周计划编号，0表示无效
        struPlanTemCfg.dwHolidayGroupNo[0] = 0;//假日组编号，按值表示，采用紧凑型排列，中间遇到0则后续无效

        byte[] byTemplateName;
        try {
            byTemplateName = "全天计划模板".getBytes("GBK");
            //计划模板名称
            for (int i = 0; i < HikConstant.NAME_LEN; i++) {
                struPlanTemCfg.byTemplateName[i] = 0;
            }
            System.arraycopy(byTemplateName, 0, struPlanTemCfg.byTemplateName, 0, byTemplateName.length);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        struPlanTemCfg.write();

        IntByReference pInt = new IntByReference(0);
        Pointer lpStatusList = pInt.getPointer();
        Integer lUserID = ConfigJsonUtil.getDeviceSearchInfoByIp(ip).getLoginId();
        if (!this.hikDevService.NET_DVR_SetDeviceConfig(lUserID, HikConstant.NET_DVR_SET_CARD_RIGHT_PLAN_TEMPLATE_V50, 1, struPlanCond.getPointer(), struPlanCond.size(), lpStatusList, struPlanTemCfg.getPointer(), struPlanTemCfg.size())) {
            iErr = this.hikDevService.NET_DVR_GetLastError();
            log.info("NET_DVR_SET_CARD_RIGHT_PLAN_TEMPLATE_V50失败，错误号：" + iErr);
            return result.err("NET_DVR_SET_CARD_RIGHT_PLAN_TEMPLATE_V50失败，错误号：" + iErr);
        }
        log.info("NET_DVR_SET_CARD_RIGHT_PLAN_TEMPLATE_V50成功！");

        //获取卡权限周计划参数
        NET_DVR_WEEK_PLAN_COND struWeekPlanCond = new NET_DVR_WEEK_PLAN_COND();
        struWeekPlanCond.dwSize = struWeekPlanCond.size();
        struWeekPlanCond.dwWeekPlanNumber = 1;
        struWeekPlanCond.wLocalControllerID = 0;

        NET_DVR_WEEK_PLAN_CFG struWeekPlanCfg = new NET_DVR_WEEK_PLAN_CFG();

        struWeekPlanCond.write();
        struWeekPlanCfg.write();

        Pointer lpCond = struWeekPlanCond.getPointer();
        Pointer lpInbuferCfg = struWeekPlanCfg.getPointer();

        if (!this.hikDevService.NET_DVR_GetDeviceConfig(lUserID, HikConstant.NET_DVR_GET_CARD_RIGHT_WEEK_PLAN_V50, 1, lpCond, struWeekPlanCond.size(), lpStatusList, lpInbuferCfg, struWeekPlanCfg.size())) {
            iErr = this.hikDevService.NET_DVR_GetLastError();
            log.info("NET_DVR_GET_CARD_RIGHT_WEEK_PLAN_V50失败，错误号：" + iErr);
            return result.err("NET_DVR_GET_CARD_RIGHT_WEEK_PLAN_V50失败，错误号：" + iErr);
        }
        struWeekPlanCfg.read();

        struWeekPlanCfg.byEnable = 1; //是否使能：0- 否，1- 是

        //避免时间段交叉，先初始化
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 8; j++) {
                struWeekPlanCfg.struPlanCfg[i].struPlanCfgDay[j].byEnable = 0;
                struWeekPlanCfg.struPlanCfg[i].struPlanCfgDay[j].struTimeSegment.struBeginTime.byHour = 0;
                struWeekPlanCfg.struPlanCfg[i].struPlanCfgDay[j].struTimeSegment.struBeginTime.byMinute = 0;
                struWeekPlanCfg.struPlanCfg[i].struPlanCfgDay[j].struTimeSegment.struBeginTime.bySecond = 0;
                struWeekPlanCfg.struPlanCfg[i].struPlanCfgDay[j].struTimeSegment.struEndTime.byHour = 0;
                struWeekPlanCfg.struPlanCfg[i].struPlanCfgDay[j].struTimeSegment.struEndTime.byMinute = 0;
                struWeekPlanCfg.struPlanCfg[i].struPlanCfgDay[j].struTimeSegment.struEndTime.bySecond = 0;
            }
        }

        //一周7天，全天24小时
        for (int i = 0; i < 7; i++) {
            struWeekPlanCfg.struPlanCfg[i].struPlanCfgDay[0].byEnable = 1;
            struWeekPlanCfg.struPlanCfg[i].struPlanCfgDay[0].struTimeSegment.struBeginTime.byHour = 0;
            struWeekPlanCfg.struPlanCfg[i].struPlanCfgDay[0].struTimeSegment.struBeginTime.byMinute = 0;
            struWeekPlanCfg.struPlanCfg[i].struPlanCfgDay[0].struTimeSegment.struBeginTime.bySecond = 0;
            struWeekPlanCfg.struPlanCfg[i].struPlanCfgDay[0].struTimeSegment.struEndTime.byHour = 24;
            struWeekPlanCfg.struPlanCfg[i].struPlanCfgDay[0].struTimeSegment.struEndTime.byMinute = 0;
            struWeekPlanCfg.struPlanCfg[i].struPlanCfgDay[0].struTimeSegment.struEndTime.bySecond = 0;
        }

        //一周7天，每天设置2个时间段
	    /*for(int i=0;i<7;i++)
	    {
	            struWeekPlanCfg.struPlanCfg[i].struPlanCfgDay[0].byEnable = 1;
	            struWeekPlanCfg.struPlanCfg[i].struPlanCfgDay[0].struTimeSegment.struBeginTime.byHour = 0;
	            struWeekPlanCfg.struPlanCfg[i].struPlanCfgDay[0].struTimeSegment.struBeginTime.byMinute = 0;
	            struWeekPlanCfg.struPlanCfg[i].struPlanCfgDay[0].struTimeSegment.struBeginTime.bySecond = 0;
	            struWeekPlanCfg.struPlanCfg[i].struPlanCfgDay[0].struTimeSegment.struEndTime.byHour = 11;
	            struWeekPlanCfg.struPlanCfg[i].struPlanCfgDay[0].struTimeSegment.struEndTime.byMinute = 59;
	            struWeekPlanCfg.struPlanCfg[i].struPlanCfgDay[0].struTimeSegment.struEndTime.bySecond = 59;

	            struWeekPlanCfg.struPlanCfg[i].struPlanCfgDay[1].byEnable = 1;
	            struWeekPlanCfg.struPlanCfg[i].struPlanCfgDay[1].struTimeSegment.struBeginTime.byHour = 13;
	            struWeekPlanCfg.struPlanCfg[i].struPlanCfgDay[1].struTimeSegment.struBeginTime.byMinute = 30;
	            struWeekPlanCfg.struPlanCfg[i].struPlanCfgDay[1].struTimeSegment.struBeginTime.bySecond = 0;
	            struWeekPlanCfg.struPlanCfg[i].struPlanCfgDay[1].struTimeSegment.struEndTime.byHour = 19;
	            struWeekPlanCfg.struPlanCfg[i].struPlanCfgDay[1].struTimeSegment.struEndTime.byMinute = 59;
	            struWeekPlanCfg.struPlanCfg[i].struPlanCfgDay[1].struTimeSegment.struEndTime.bySecond = 59;
	    }*/
        struWeekPlanCfg.write();

        //设置卡权限周计划参数
        if (!this.hikDevService.NET_DVR_SetDeviceConfig(lUserID, HikConstant.NET_DVR_SET_CARD_RIGHT_WEEK_PLAN_V50, 1, lpCond, struWeekPlanCond.size(), lpStatusList, lpInbuferCfg, struWeekPlanCfg.size())) {
            iErr = this.hikDevService.NET_DVR_GetLastError();
            log.info("NET_DVR_SET_CARD_RIGHT_WEEK_PLAN_V50失败，错误号：" + iErr);
            return result.err("NET_DVR_SET_CARD_RIGHT_WEEK_PLAN_V50失败，错误号：" + iErr);
        }
        log.info("NET_DVR_SET_CARD_RIGHT_WEEK_PLAN_V50成功！");
        return result.ok("全天计划模板成功!");
    }

    private boolean delCard(String ip, String cardNo) {
        NET_DVR_CARD_COND struCardCond = new NET_DVR_CARD_COND();
        struCardCond.read();
        struCardCond.dwSize = struCardCond.size();
        struCardCond.dwCardNum = 1;  //下发一张
        struCardCond.write();
        Pointer ptrStruCond = struCardCond.getPointer();
        Integer longUserId = ConfigJsonUtil.getDeviceSearchInfoByIp(ip).getLoginId();
        int m_lSetCardCfgHandle = this.hikDevService.NET_DVR_StartRemoteConfig(longUserId, HikConstant.NET_DVR_DEL_CARD, ptrStruCond, struCardCond.size(), null, null);
        if (m_lSetCardCfgHandle == -1) {
            log.info("建立删除卡长连接失败，错误码为" + this.hikDevService.NET_DVR_GetLastError());
            return false;
        } else {
            log.info("建立删除卡长连接成功！");
        }

        NET_DVR_CARD_SEND_DATA struCardData = new NET_DVR_CARD_SEND_DATA();
        struCardData.read();
        struCardData.dwSize = struCardData.size();

        for (int i = 0; i < HikConstant.ACS_CARD_NO_LEN; i++) {
            struCardData.byCardNo[i] = 0;
        }
        for (int i = 0; i < cardNo.length(); i++) {
            struCardData.byCardNo[i] = cardNo.getBytes()[i];
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
                log.info("NET_DVR_SendWithRecvRemoteConfig接口调用失败，错误码：" + this.hikDevService.NET_DVR_GetLastError());
                return false;
            } else if (dwState == HikConstant.NET_SDK_CONFIG_STATUS_NEEDWAIT) {
                log.info("配置等待");
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if (dwState == HikConstant.NET_SDK_CONFIG_STATUS_FAILED) {
                log.info("删除卡失败, 卡号: " + new String(struCardStatus.byCardNo).trim() + ", 错误码：" + struCardStatus.dwErrorCode);
                return false;
            } else if (dwState == HikConstant.NET_SDK_CONFIG_STATUS_EXCEPTION) {
                log.info("删除卡异常, 卡号: " + new String(struCardStatus.byCardNo).trim() + ", 错误码：" + struCardStatus.dwErrorCode);
                return false;
            } else if (dwState == HikConstant.NET_SDK_CONFIG_STATUS_SUCCESS) {
                if (struCardStatus.dwErrorCode != 0) {
                    log.info("删除卡成功,但是错误码" + struCardStatus.dwErrorCode + ", 卡号：" + new String(struCardStatus.byCardNo).trim());
                } else {
                    log.info("删除卡成功, 卡号: " + new String(struCardStatus.byCardNo).trim() + ", 状态：" + struCardStatus.byStatus);
                }
            } else if (dwState == HikConstant.NET_SDK_CONFIG_STATUS_FINISH) {
                log.info("删除卡完成");
                break;
            }
        }

        if (!this.hikDevService.NET_DVR_StopRemoteConfig(m_lSetCardCfgHandle)) {
            log.info("NET_DVR_StopRemoteConfig接口调用失败，错误码：" + this.hikDevService.NET_DVR_GetLastError());
            return false;
        } else {
            log.info("NET_DVR_StopRemoteConfig接口成功");
        }
        return true;
    }

    private boolean delCardFace(String ip, String cardNo) {
//        HikDevResponse result = new HikDevResponse();
        NET_DVR_FACE_PARAM_CTRL struFaceDelCond = new NET_DVR_FACE_PARAM_CTRL();
        struFaceDelCond.dwSize = struFaceDelCond.size();
        //删除方式：0- 按卡号方式删除，1- 按读卡器删除
        struFaceDelCond.byMode = 0;

        struFaceDelCond.struProcessMode.setType(NET_DVR_FACE_PARAM_BYCARD.class);

        //需要删除人脸关联的卡号
        for (int i = 0; i < HikConstant.ACS_CARD_NO_LEN; i++) {
            struFaceDelCond.struProcessMode.struByCard.byCardNo[i] = 0;
        }
        System.arraycopy(cardNo.getBytes(), 0, struFaceDelCond.struProcessMode.struByCard.byCardNo, 0, cardNo.length());
        //读卡器
        struFaceDelCond.struProcessMode.struByCard.byEnableCardReader[0] = 1;
        //人脸ID
        struFaceDelCond.struProcessMode.struByCard.byFaceID[0] = 1;
        struFaceDelCond.write();

        Pointer ptrFaceDelCond = struFaceDelCond.getPointer();
        Integer longUserId = ConfigJsonUtil.getDeviceSearchInfoByIp(ip).getLoginId();
        boolean bRet = this.hikDevService.NET_DVR_RemoteControl(longUserId, HikConstant.NET_DVR_DEL_FACE_PARAM_CFG, ptrFaceDelCond, struFaceDelCond.size());
        if (!bRet) {
            log.info("删除人脸失败，错误码为" + this.hikDevService.NET_DVR_GetLastError());
//            result.err( "删除人脸失败，错误码为" + this.hikDevService.NET_DVR_GetLastError());
            return false;
        } else {
            log.info("删除人脸成功！");
//            result.ok( "删除人脸成功！");
            return true;
        }
//        return result;
    }

    private JSONObject userInfo(String ip, String urlInBuffer, Pointer lpInBuff, int dwInBuffSize, int dwCommand, int iOutBuffLen) {
        JSONObject result = new JSONObject();
        Integer lHandler = this.startRemoteConfig(ip, urlInBuffer, dwCommand);
        if (lHandler < 0) {
            result.put("msg", "NET_DVR_StartRemoteConfig 接口调用失败，错误码：" + this.hikDevService.NET_DVR_GetLastError());
            return result;
        }
        BYTE_ARRAY ptrOutBuff = new BYTE_ARRAY(iOutBuffLen);
        IntByReference pInt = new IntByReference(0);
        int dwState = this.hikDevService.NET_DVR_SendWithRecvRemoteConfig(lHandler, lpInBuff, dwInBuffSize, ptrOutBuff.getPointer(), iOutBuffLen, pInt);
        //读取返回的json并解析
        ptrOutBuff.read();
        String strResult = new String(ptrOutBuff.byValue).trim();
        JSONObject jsonResult = JSONObject.parseObject(strResult);
        result.put("data", jsonResult);
        if (dwState != HikConstant.NET_SDK_CONFIG_STATUS_SUCCESS) {
            //返回NET_SDK_CONFIG_STATUS_SUCCESS代表流程走通了，但并不代表下发成功，比如有些设备可能因为人员已存在等原因下发失败，所以需要解析Json报文 如果statusCode=1无异常情况,否则就是有异常情况
            result.put("msg", "NET_DVR_SendWithRecvRemoteConfig接口调用失败，错误码：" + this.hikDevService.NET_DVR_GetLastError());
        }
        // TODO 下发人员时：dwState其实不会走到这里，因为设备不知道我们会下发多少个人，所以长连接需要我们主动关闭
        if (!this.hikDevService.NET_DVR_StopRemoteConfig(lHandler)) {
            result.put("msg", "NET_DVR_StopRemoteConfig接口调用失败，错误码：" + this.hikDevService.NET_DVR_GetLastError());
        }
        return result;
    }

    private Integer startRemoteConfig(String ip, String urlInBuffer, int dwCommand) {
        // 获取用户句柄

        Integer longUserId = Objects.requireNonNull(ConfigJsonUtil.getDeviceSearchInfoByIp(ip)).getLoginId();
        if (null != longUserId && longUserId != -1) {
            //  数组
            BYTE_ARRAY ptrByteArray = new BYTE_ARRAY(1024);
            ptrByteArray.byValue = urlInBuffer.getBytes();
            ptrByteArray.write();

            return this.hikDevService.NET_DVR_StartRemoteConfig(longUserId, dwCommand, ptrByteArray.getPointer(), urlInBuffer.length(), null, null);
        }
        return -1;
    }

    private JSONObject aboutUserInfo(String ip, AccessPeople people, String urlInBuffer) {
        //name需要转为字节数组 将中文字符编码之后用数组拷贝的方式，避免因为编码导致的长度问题
//        people.setName(people.getRealName().getBytes(StandardCharsets.UTF_8));
        people.setName(people.getRealName());
        people.setValid(new Valid());
        List<RightPlan> rightPlanList = new ArrayList<>();
        rightPlanList.add(new RightPlan());
        people.setRightPlan(rightPlanList);
        JSONObject userInfoBuff = new JSONObject();
        userInfoBuff.put("UserInfo", people);
        String userInfoBuffString = userInfoBuff.toJSONString();
        BYTE_ARRAY ptrInBuff = new BYTE_ARRAY(userInfoBuffString.length());
        ptrInBuff.byValue = userInfoBuffString.getBytes(StandardCharsets.UTF_8);
        ptrInBuff.write();
        return this.userInfo(ip, urlInBuffer, ptrInBuff.getPointer(), userInfoBuffString.length(), 2550, 1024);
    }

    private HikDevResponse deleteOperation(String ip, String urlInBuffer, String strInBuffer) {
        BYTE_ARRAY ptrUrl = new BYTE_ARRAY(HikConstant.BYTE_ARRAY_LEN);
        ptrUrl.byValue = urlInBuffer.getBytes(StandardCharsets.UTF_8);
        ptrUrl.write();

        //输入条件
        log.info("打印json封装：{}", strInBuffer);
        BYTE_ARRAY ptrInBuffer = new BYTE_ARRAY(HikConstant.ISAPI_DATA_LEN);
        ptrInBuffer.read();
        ptrInBuffer.byValue = strInBuffer.getBytes(StandardCharsets.UTF_8);
        ptrInBuffer.write();

        // 获取用户句柄
        Integer longUserId = ConfigJsonUtil.getDeviceSearchInfoByIp(ip).getLoginId();

        NET_DVR_XML_CONFIG_INPUT strXMLInput = new NET_DVR_XML_CONFIG_INPUT();
        strXMLInput.read();
        strXMLInput.dwSize = strXMLInput.size();
        strXMLInput.lpRequestUrl = ptrUrl.getPointer();
        strXMLInput.dwRequestUrlLen = ptrUrl.byValue.length;
        strXMLInput.lpInBuffer = ptrInBuffer.getPointer();
        strXMLInput.dwInBufferSize = ptrInBuffer.byValue.length;
        strXMLInput.write();
        BYTE_ARRAY ptrStatusByte = new BYTE_ARRAY(HikConstant.ISAPI_STATUS_LEN);
        ptrStatusByte.read();
        BYTE_ARRAY ptrOutByte = new BYTE_ARRAY(HikConstant.ISAPI_DATA_LEN);
        ptrOutByte.read();
        NET_DVR_XML_CONFIG_OUTPUT strXMLOutput = new NET_DVR_XML_CONFIG_OUTPUT();
        strXMLOutput.read();
        strXMLOutput.dwSize = strXMLOutput.size();
        strXMLOutput.lpOutBuffer = ptrOutByte.getPointer();
        strXMLOutput.dwOutBufferSize = ptrOutByte.size();
        strXMLOutput.lpStatusBuffer = ptrStatusByte.getPointer();
        strXMLOutput.dwStatusSize = ptrStatusByte.size();
        strXMLOutput.write();

        HikDevResponse result = new HikDevResponse();
        if (!this.hikDevService.NET_DVR_STDXMLConfig(longUserId, strXMLInput, strXMLOutput)) {
            int iErr = this.hikDevService.NET_DVR_GetLastError();
            return result.err("NET_DVR_STDXMLConfig失败，错误号：" + iErr);
        } else {
            strXMLOutput.read();
            ptrOutByte.read();
            ptrStatusByte.read();
            String strOutXML = new String(ptrOutByte.byValue).trim();
            String strStatus = new String(ptrStatusByte.byValue).trim();
            JSONObject strJson = new JSONObject();
            strJson.put("strOutXMl", JSONObject.parseObject(strOutXML));
            strJson.put("strStatus", JSONObject.parseObject(strStatus));
            result.ok("操作成功", strJson);
            log.info("输出结果:" + strJson);
        }
        return result;
    }

}
