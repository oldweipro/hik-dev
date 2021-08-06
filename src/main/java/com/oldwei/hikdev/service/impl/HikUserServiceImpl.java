package com.oldwei.hikdev.service.impl;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.oldwei.hikdev.constant.HikConstant;
import com.oldwei.hikdev.constant.DataCachePrefixConstant;
import com.oldwei.hikdev.entity.access.AccessPeople;
import com.oldwei.hikdev.entity.QueryRequest;
import com.oldwei.hikdev.entity.access.RightPlan;
import com.oldwei.hikdev.entity.access.UserInfoSearch;
import com.oldwei.hikdev.entity.access.Valid;
import com.oldwei.hikdev.service.IHikDevService;
import com.oldwei.hikdev.service.IHikUserService;
import com.oldwei.hikdev.structure.BYTE_ARRAY;
import com.oldwei.hikdev.structure.NET_DVR_JSON_DATA_CFG;
import com.oldwei.hikdev.structure.NET_DVR_XML_CONFIG_INPUT;
import com.oldwei.hikdev.structure.NET_DVR_XML_CONFIG_OUTPUT;
import com.oldwei.hikdev.component.DataCache;
import com.oldwei.hikdev.component.FileStream;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangjie
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HikUserServiceImpl implements IHikUserService {
    private final IHikDevService hikDevService;
    private final DataCache dataCache;
    private final FileStream fileStream;

    @Override
    public JSONObject getAbility(JSONObject jsonObject) {
        String urlInBuffer = "GET /ISAPI/AccessControl/UserInfo/capabilities?format=json";
        String strInBuffer = "";
        return 删除操作(jsonObject.getString("ip"), urlInBuffer, strInBuffer);
    }

    @Override
    public JSONObject searchUserInfo(String ip, String[] employeeNos, QueryRequest queryRequest) {
        String urlInBuffer = "POST /ISAPI/AccessControl/UserInfo/Search?format=json";

        //=====================================================================
        //组装查询的JSON报文，这边查询的是所有的卡
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonSearchCond = new JSONObject();

        //如果需要查询指定的工号人员信息 EmployeeNoList可选，人员 ID 列表
        if (employeeNos.length > 0) {
            jsonSearchCond.put("EmployeeNoList", 封装用户ID(employeeNos));
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
        log.info("查询人员的json报文:{}", strInBuff);
        //=====================================================================
        BYTE_ARRAY ptrInBuff = new BYTE_ARRAY(strInBuff.length());
        ptrInBuff.byValue = strInBuff.getBytes(StandardCharsets.UTF_8);
        ptrInBuff.write();
        JSONObject result = this.userInfo(ip, urlInBuffer, ptrInBuff.getPointer(), strInBuff.length(), 2550, 1024 * 10);

        //因为name是字节数组，所以需要自己转一下
        UserInfoSearch userInfoSearch = result.getJSONObject("data").getJSONObject("UserInfoSearch").toJavaObject(UserInfoSearch.class);
        if (null != userInfoSearch.getUserInfo() && userInfoSearch.getUserInfo().size() > 0) {
            userInfoSearch.getUserInfo().forEach(people -> {
                try {
                    //这里必须加上双引号反序列化，因为在序列化的时候是json带双引号
                    String nameByte = "\"" + people.getRealName() + "\"";
                    //如果realName是json序列化的字符串，那么需要反序列化
                    byte[] bytes = JSON.parseObject(nameByte, byte[].class);
                    String realName = new String(bytes).trim();
                    people.setRealName(realName);
                } catch (ArrayIndexOutOfBoundsException ignored) {
                    // 汉字是越界异常超出256，不加上瘾好是expect '[', but error, pos 1, line 1, column 2
                    // 已经是汉字的名字，也就是说他们的名字是从设备上直接录制的，并没有走sdk，
                    // 这时候就会反序列化错误，那么我们就直接忽略这些错误，把realName原样输出
                }

            });
        }
        result.put("data", userInfoSearch);
        return result;
    }

    @Override
    public JSONObject addUserInfo(String ip, AccessPeople people) {
        String urlInBuffer = "POST /ISAPI/AccessControl/UserInfo/Record?format=json";
        return this.aboutUserInfo(ip, people, urlInBuffer);
    }

    @Override
    public JSONObject modifyUserInfo(String ip, AccessPeople people) {
        String urlInBuffer = "PUT /ISAPI/AccessControl/UserInfo/Modify?format=json";
        return this.aboutUserInfo(ip, people, urlInBuffer);
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

    @Override
    public JSONObject addMultiUserInfo(String ip, List<AccessPeople> peopleList) {
        JSONObject result = new JSONObject();
        for (AccessPeople people : peopleList) {
            result = this.addUserInfo(ip, people);
        }
        return result;
    }

    @Override
    public JSONObject searchFaceInfo(String ip, AccessPeople people) {
        String urlInBuffer = "POST /ISAPI/Intelligent/FDLib/FDSearch?format=json";
        //数据参数组装====================================
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("searchResultPosition", 0);
        jsonObject.put("maxResults", 1);
        jsonObject.put("faceLibType", "blackFD");
        jsonObject.put("FDID", "1");
        //人脸关联的工号，同下发人员时的employeeNo字段
        jsonObject.put("FPID", people.getEmployeeNo());

        String strInBuffer = jsonObject.toJSONString();
        log.info("查询人脸的json报文:" + strInBuffer);
        //=================================

        NET_DVR_JSON_DATA_CFG strJsonData = new NET_DVR_JSON_DATA_CFG();
        strJsonData.write();
//        JSONObject result = this.userInfo(ip, urlInBuffer, strInBuffer);
        JSONObject result = new JSONObject();
        Integer lHandler = this.startRemoteConfig(ip, urlInBuffer, 2552);
        if (lHandler < 0) {
            result.put("code", -1);
            result.put("msg", "NET_DVR_StartRemoteConfig 失败,错误码为" + this.hikDevService.NET_DVR_GetLastError());
            return result;
        } else {
            //把string传递到Byte数组中，后续用.getPointer()方法传入指针地址中。
            BYTE_ARRAY ptrInbuff = new BYTE_ARRAY(strInBuffer.length());
            System.arraycopy(strInBuffer.getBytes(), 0, ptrInbuff.byValue, 0, strInBuffer.length());
            ptrInbuff.write();

            IntByReference pInt = new IntByReference(0);
            int dwState = this.hikDevService.NET_DVR_SendWithRecvRemoteConfig(lHandler, ptrInbuff.getPointer(), strInBuffer.length(), strJsonData.getPointer(), strJsonData.size(), pInt);
            strJsonData.read();
            if (dwState == -1 || dwState == HikConstant.NET_SDK_CONFIG_STATUS_FAILED || dwState == HikConstant.NET_SDK_CONFIG_STATUS_EXCEPTION) {
                result.put("code", -1);
                result.put("msg", "查询人脸失败/异常 NET_DVR_SendWithRecvRemoteConfig接口调用失败，错误码：" + this.hikDevService.NET_DVR_GetLastError());
            } else if (dwState == HikConstant.NET_SDK_CONFIG_STATUS_NEEDWAIT) {
                log.info("配置等待");
            } else if (dwState == HikConstant.NET_SDK_CONFIG_STATUS_SUCCESS) {
                //解析JSON字符串
                BYTE_ARRAY pJsonData = new BYTE_ARRAY(strJsonData.dwJsonDataSize);
                pJsonData.write();
                Pointer pPlateInfo = pJsonData.getPointer();
                pPlateInfo.write(0, strJsonData.lpJsonData.getByteArray(0, pJsonData.size()), 0, pJsonData.size());
                pJsonData.read();
                String strResult = new String(pJsonData.byValue).trim();
                System.out.println("strResult:" + strResult);
                JSONObject jsonResult = JSONObject.parseObject(strResult);

                int numOfMatches = jsonResult.getIntValue("numOfMatches");
                //确认有人脸
                // TODO 还没有返回信息
                if (numOfMatches != 0) {
                    String filename = this.fileStream.touchJpg();
                    log.info(filename);
                    this.fileStream.downloadToLocal(filename, strJsonData.lpPicData.getByteArray(0, strJsonData.dwPicDataSize));
                }
            } else if (dwState == HikConstant.NET_SDK_CONFIG_STATUS_FINISH) {
                log.info("获取人脸完成");
            }

            if (!this.hikDevService.NET_DVR_StopRemoteConfig(lHandler)) {
                result.put("code", -1);
                result.put("msg", "NET_DVR_StopRemoteConfig接口调用失败，错误码：" + this.hikDevService.NET_DVR_GetLastError());
            } else {
                log.info("NET_DVR_StopRemoteConfig接口成功");
            }

        }
        return result;
    }

    @Override
    public JSONObject addPeopleFace(String ip, AccessPeople people) {
        String urlInBuffer = "POST /ISAPI/Intelligent/FDLib/FaceDataRecord?format=json";
        Integer lHandler = this.startRemoteConfig(ip, urlInBuffer, 2551);
        JSONObject result = new JSONObject();
        if (lHandler < 0) {
            result.put("code", -1);
            result.put("msg", "NET_DVR_StartRemoteConfig 接口调用失败，错误码：" + this.hikDevService.NET_DVR_GetLastError());
            return result;
        }
        NET_DVR_JSON_DATA_CFG strAddFaceDataCfg = new NET_DVR_JSON_DATA_CFG();
        strAddFaceDataCfg.read();
        strAddFaceDataCfg.dwSize = strAddFaceDataCfg.size();
        //字符串拷贝到数组中
        BYTE_ARRAY ptrByteArray = new BYTE_ARRAY(1024);

        String employeeNo = people.getEmployeeNo();
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
        String base64Pic = people.getBase64Pic();
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
        if (dwState == HikConstant.NET_SDK_CONFIG_STATUS_SUCCESS) {
            //返回NET_SDK_CONFIG_STATUS_SUCCESS代表流程走通了，但并不代表下发成功，比如有些设备可能因为人员已存在等原因下发失败，所以需要解析Json报文
            result.put("code", 0);
            result.put("msg", "如果statusCode=1无异常情况,否则就是有异常情况");
        } else {
            result.put("code", -1);
            result.put("msg", "NET_DVR_SendWithRecvRemoteConfig接口调用失败，错误码：" + this.hikDevService.NET_DVR_GetLastError());
        }
        // TODO 下发人员时：dwState其实不会走到这里，因为设备不知道我们会下发多少个人，所以长连接需要我们主动关闭
        if (!this.hikDevService.NET_DVR_StopRemoteConfig(lHandler)) {
            result.put("code", -1);
            result.put("msg", "NET_DVR_StopRemoteConfig接口调用失败，错误码：" + this.hikDevService.NET_DVR_GetLastError());
        }
        return result;
    }

    @Override
    public JSONObject addMultiFace(String ip, List<AccessPeople> peopleList) {
        JSONObject result = new JSONObject();
        for (AccessPeople people : peopleList) {
            result = addPeopleFace(ip, people);
        }
        return result;
    }

    @Override
    public JSONObject delFaceInfo(String ip, String[] employeeIds) {
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


        return 删除操作(ip, urlInBuffer, strInBuffer);
    }

    @Override
    public JSONObject delUserInfo(String ip, String[] employeeIds) {
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

        return 删除操作(ip, urlInBuffer, strInBuffer);
    }

    private Integer startRemoteConfig(String ip, String urlInBuffer, int dwCommand) {
        // 获取用户句柄
        Integer longUserId = this.dataCache.getInteger(DataCachePrefixConstant.HIK_REG_USERID_IP + ip);
        if (null != longUserId && longUserId != -1) {
            //  数组
            BYTE_ARRAY ptrByteArray = new BYTE_ARRAY(1024);
            ptrByteArray.byValue = urlInBuffer.getBytes();
            ptrByteArray.write();

            return this.hikDevService.NET_DVR_StartRemoteConfig(longUserId, dwCommand, ptrByteArray.getPointer(), urlInBuffer.length(), null, null);
        }
        return -1;
    }

    private List<Map<String, Object>> 封装用户ID(String[] employeeNos) {
        List<Map<String, Object>> employeeNoList = new ArrayList<>();
        for (String no : employeeNos) {
            Map<String, Object> employeeNo = new HashMap<>(1);
            //employeeNo可选，string，人员 ID
            employeeNo.put("employeeNo", no);
            employeeNoList.add(employeeNo);
        }
        return employeeNoList;
    }

    private JSONObject 删除操作(String ip, String urlInBuffer, String strInBuffer) {

        BYTE_ARRAY ptrUrl = new BYTE_ARRAY(HikConstant.BYTE_ARRAY_LEN);
        ptrUrl.byValue = urlInBuffer.getBytes(StandardCharsets.UTF_8);
        ptrUrl.write();

        //输入条件
        log.info("打印json封装：{}", strInBuffer);
        BYTE_ARRAY ptrInBuffer = new BYTE_ARRAY(HikConstant.ISAPI_DATA_LEN);
        ptrInBuffer.read();
        ptrInBuffer.byValue = strInBuffer.getBytes(StandardCharsets.UTF_8);
        ptrInBuffer.write();

        JSONObject result = new JSONObject();
        // 获取用户句柄
        Integer longUserId = this.dataCache.getInteger(DataCachePrefixConstant.HIK_REG_USERID_IP + ip);
        if (null == longUserId) {
            result.put("code", -1);
            result.put("msg", "设备状态未注册！");
            return result;
        }

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

        if (!this.hikDevService.NET_DVR_STDXMLConfig(longUserId, strXMLInput, strXMLOutput)) {
            int iErr = this.hikDevService.NET_DVR_GetLastError();
            result.put("code", -1);
            result.put("msg", "NET_DVR_STDXMLConfig失败，错误号：" + iErr);
            return result;
        } else {
            strXMLOutput.read();
            ptrOutByte.read();
            ptrStatusByte.read();
            String strOutXML = new String(ptrOutByte.byValue).trim();
            String strStatus = new String(ptrStatusByte.byValue).trim();
            JSONObject strJson = new JSONObject();
            strJson.put("strOutXMl", JSONObject.parseObject(strOutXML));
            strJson.put("strStatus", JSONObject.parseObject(strStatus));
            result.put("code", 0);
            result.put("msg", "操作成功");
            result.put("data", strJson);
            log.info("输出结果:" + strJson);
        }
        return result;
    }

    private JSONObject userInfo(String ip, String urlInBuffer, Pointer lpInBuff, int dwInBuffSize, int dwCommand, int iOutBuffLen) {

        JSONObject result = new JSONObject();
        Integer lHandler = this.startRemoteConfig(ip, urlInBuffer, dwCommand);
        if (lHandler < 0) {
            result.put("code", -1);
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
        if (dwState == HikConstant.NET_SDK_CONFIG_STATUS_SUCCESS) {
            //返回NET_SDK_CONFIG_STATUS_SUCCESS代表流程走通了，但并不代表下发成功，比如有些设备可能因为人员已存在等原因下发失败，所以需要解析Json报文
            result.put("code", 0);
            result.put("msg", "如果statusCode=1无异常情况,否则就是有异常情况");
        } else {
            result.put("code", -1);
            result.put("msg", "NET_DVR_SendWithRecvRemoteConfig接口调用失败，错误码：" + this.hikDevService.NET_DVR_GetLastError());
        }
        // TODO 下发人员时：dwState其实不会走到这里，因为设备不知道我们会下发多少个人，所以长连接需要我们主动关闭
        if (!this.hikDevService.NET_DVR_StopRemoteConfig(lHandler)) {
            result.put("code", -1);
            result.put("msg", "NET_DVR_StopRemoteConfig接口调用失败，错误码：" + this.hikDevService.NET_DVR_GetLastError());
        }
        return result;
    }
}
