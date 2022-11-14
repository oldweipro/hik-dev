package com.oldwei.hikdev.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.oldwei.hikdev.constant.HikConstant;
import com.oldwei.hikdev.entity.config.*;
import com.oldwei.hikdev.service.IHikAlarmDataService;
import com.oldwei.hikdev.service.IHikDevService;
import com.oldwei.hikdev.service.IHikDeviceService;
import com.oldwei.hikdev.structure.*;
import com.oldwei.hikdev.util.ConfigJsonUtil;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author oldwei
 * @date 2021-5-19 13:44
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HikDeviceServiceImpl implements IHikDeviceService {

    private final IHikDevService hikDevService;
    private final IHikAlarmDataService hikAlarmDataService;

    @Override
    public boolean clean(String ip) {
        DeviceSearchInfo deviceLoginByIp = ConfigJsonUtil.getDeviceSearchInfoByIp(ip);
        if (ObjectUtil.isNotNull(deviceLoginByIp)) {
            Integer longUserId = deviceLoginByIp.getLoginId();
            if (deviceLoginByIp.getAlarmHandleId() > -1) {
                // 撤防
                this.hikAlarmDataService.closeAlarmChan(ip);
            }
            if (deviceLoginByIp.getPreviewHandleId() > -1) {

            }
            //退出的时候注销\释放SDK资源
            if (null != longUserId && longUserId != -1) {
                // 删除config中的这段数据
                ConfigJsonUtil.removeDeviceLogin(ip);
                return hikDevService.NET_DVR_Logout(longUserId);
            }

        }
        return true;
        //退出程序时调用注销登录、反初始化接口、销毁sdk初始化
//        return hikDevService.NET_DVR_Cleanup();
    }

    /**
     * 设备注册
     *
     * @param deviceLogin
     * @return
     */
    @Override
    public synchronized boolean login(DeviceLoginDTO deviceLogin) {
        AtomicBoolean callback = new AtomicBoolean(true);
        // 初始化设备登录信息
        NET_DVR_USER_LOGIN_INFO netDvrUserLoginInfo = new NET_DVR_USER_LOGIN_INFO();
        //设备ip地址
        System.arraycopy(deviceLogin.getIpv4Address().getBytes(), 0, netDvrUserLoginInfo.sDeviceAddress, 0, deviceLogin.getIpv4Address().length());
        //设备用户名
        System.arraycopy(deviceLogin.getUsername().getBytes(), 0, netDvrUserLoginInfo.sUserName, 0, deviceLogin.getUsername().length());
        //设备密码
        System.arraycopy(deviceLogin.getPassword().getBytes(), 0, netDvrUserLoginInfo.sPassword, 0, deviceLogin.getPassword().length());
        //设备端口
        netDvrUserLoginInfo.wPort = Short.parseShort(deviceLogin.getCommandPort());
        netDvrUserLoginInfo.cbLoginResult = (int lUserID, int dwResult, NET_DVR_DEVICEINFO_V30 lpDeviceinfo, Pointer pUser) -> {
            deviceLogin.setLoginId(lUserID);
            int ipChan = lpDeviceinfo.byIPChanNum + lpDeviceinfo.byHighDChanNum * 256;
            List<Integer> allIds = new ArrayList<>();
            List<DeviceChannel> allDeviceChannels = new ArrayList<>();
            if (lpDeviceinfo.byChanNum > 0) {
                // 模拟通道
                List<Integer> analogIds = new ArrayList<>();
                List<DeviceChannel> deviceChannels = new ArrayList<>();
                for (int i = 0; i < lpDeviceinfo.byStartChan; i++) {
                    // 遍历通道号
                    Integer channelId = lpDeviceinfo.byStartChan + i;
                    analogIds.add(channelId);
                    DeviceChannel deviceChannel = new DeviceChannel();
                    deviceChannel.setChannelId(channelId);
                    deviceChannel.setByEnable(1);
                    deviceChannel.setRtspStream("rtsp://" + deviceLogin.getUsername() + ":" + deviceLogin.getPassword() + "@" + deviceLogin.getIpv4Address() + ":554/h264/ch" + channelId + "/main/av_stream");
                    deviceChannels.add(deviceChannel);
                }
                allIds.addAll(analogIds);
                allDeviceChannels.addAll(deviceChannels);
                deviceLogin.setAnalogChannelIds(analogIds);
            }
            if (ipChan > 0) {
                // 数字IP通道
                List<Integer> digitalIds = new ArrayList<>();
                for (int i = 0; i < ipChan; i++) {
                    // 遍历通道号
                    Integer channelId = lpDeviceinfo.byStartDChan + i;
                    digitalIds.add(channelId);
                }
                allIds.addAll(digitalIds);
                deviceLogin.setDigitalChannelIds(digitalIds);
            }
            deviceLogin.setAllChannelIds(allIds);
            deviceLogin.setDeviceChannels(allDeviceChannels);
            callback.set(false);
            return 1;
        };
        //是否异步登录：0- 否，1- 是 默认false
        netDvrUserLoginInfo.bUseAsynLogin = deviceLogin.getUseAsync();
        netDvrUserLoginInfo.write();

        //设备信息
        NET_DVR_DEVICEINFO_V40 netDvrDeviceInfoV40 = new NET_DVR_DEVICEINFO_V40();
        //用户句柄
        int longUserId = hikDevService.NET_DVR_Login_V40(netDvrUserLoginInfo, netDvrDeviceInfoV40);
        if (longUserId < 0) {
            log.info("登录失败，错误码为:" + hikDevService.NET_DVR_GetLastError());
            return false;
        } else {
            deviceLogin.setLoginId(longUserId);
            //设备字符集
            int iCharEncodeType = netDvrDeviceInfoV40.byCharEncodeType;
            deviceLogin.setCharEncodeType(iCharEncodeType);
            while (callback.get()) {
            }
            NET_DVR_IPPARACFG_V40 netDvrIpparacfgV40 = new NET_DVR_IPPARACFG_V40();
            Pointer pointer = netDvrIpparacfgV40.getPointer();
            IntByReference lpSizeReturned = new IntByReference();
            netDvrIpparacfgV40.write();
            boolean b = this.hikDevService.NET_DVR_GetDVRConfig(longUserId,
                    HikConstant.NET_DVR_GET_IPPARACFG_V40,
                    0,
                    pointer,
                    netDvrIpparacfgV40.size(),
                    lpSizeReturned);
            netDvrIpparacfgV40.read();
            int in = this.hikDevService.NET_DVR_GetLastError();
            if (in == 0) {
                List<DeviceChannel> deviceChannels = new ArrayList<>();
                for (int iChannum = 0; iChannum < netDvrIpparacfgV40.dwDChanNum; iChannum++) {
                    int channum = iChannum + netDvrIpparacfgV40.dwStartDChan;
                    netDvrIpparacfgV40.struStreamMode[iChannum].read();
                    if (netDvrIpparacfgV40.struStreamMode[iChannum].byGetStreamType == 0) {
                        netDvrIpparacfgV40.struStreamMode[iChannum].uGetStream.setType(NET_DVR_IPCHANINFO.class);
                        netDvrIpparacfgV40.struStreamMode[iChannum].uGetStream.struChanInfo.read();
                        DeviceChannel deviceChannel = new DeviceChannel();
                        deviceChannel.setByEnable((int) netDvrIpparacfgV40.struStreamMode[iChannum].uGetStream.struChanInfo.byEnable);
                        deviceChannel.setChannelId(channum);
                        deviceChannel.setRtspStream("rtsp://" + deviceLogin.getUsername() + ":" + deviceLogin.getPassword() + "@" + deviceLogin.getIpv4Address() + ":554/h264/ch" + channum + "/main/av_stream");
                        deviceChannels.add(deviceChannel);
//                        if (netDvrIpparacfgV40.struStreamMode[iChannum].uGetStream.struChanInfo.byEnable == 1) {
//                            System.out.println("IP通道" + channum + "在线");
//                        } else {
//                            System.out.println("IP通道" + channum + "不在线");
//                        }
                    }
                }
                deviceLogin.setDeviceChannels(deviceChannels);
            } else {
                if (in == 23) {
//                    log.info("{} 不支持IP通道", deviceLogin.getIpv4Address());
                } else {
                    log.info("错误码: {}", in);
                }

            }


            //获取设备类型
            NET_DVR_DEVICECFG netDvrDevicecfg = new NET_DVR_DEVICECFG();
            Pointer netDvrDevicecfgcfgPointer = netDvrDevicecfg.getPointer();
            IntByReference lpSizeReturnedcfg = new IntByReference();
            netDvrDevicecfg.write();
            hikDevService.NET_DVR_GetDVRConfig(longUserId, HikConstant.NET_DVR_GET_DEVICECFG, 0xffffffff, netDvrDevicecfgcfgPointer, netDvrDevicecfg.size(), lpSizeReturnedcfg);
            netDvrDevicecfg.read();
            int incfg = this.hikDevService.NET_DVR_GetLastError();
            if (incfg == 0) {
//                System.out.println("byDVRType: " + netDvrDevicecfg.byDVRType);
                switch (netDvrDevicecfg.byDVRType) {//目前仅维护可测试设备,补充设备类型查询文档NET_DVR_DEVICECFG的byDVRType属性
                    case 90: // DS90XX_HF_S
                        deviceLogin.setDVRType(HikConstant.DS90XX_HF_S);
                        break;
                    default:
                }
            } else {
                log.info("错误码: {}", incfg);
            }


            if (netDvrDevicecfg.byDVRType == 0 && StrUtil.isEmpty(deviceLogin.getDVRType())) {
                //获取设备类型(扩展结构)
                NET_DVR_DEVICECFG_V40 netDvrDevicecfgV40 = new NET_DVR_DEVICECFG_V40();
                Pointer netDvrDevicecfgV40Pointer = netDvrDevicecfgV40.getPointer();
                IntByReference lpSizeReturnedV40 = new IntByReference();
                netDvrDevicecfgV40.write();
                hikDevService.NET_DVR_GetDVRConfig(longUserId, HikConstant.NET_DVR_GET_DEVICECFG_V40, 0xffffffff, netDvrDevicecfgV40Pointer, netDvrDevicecfgV40.size(), lpSizeReturnedV40);
                netDvrDevicecfgV40.read();
                int inV40 = this.hikDevService.NET_DVR_GetLastError();
                if (inV40 == 0) {
//                    System.out.println("byDVRType: " + netDvrDevicecfgV40.byDVRType);
//                    System.out.println("wDevType: " + netDvrDevicecfgV40.wDevType);
//                    System.out.println("byDevTypeName: " + Arrays.toString(netDvrDevicecfgV40.byDevTypeName));
                    switch (netDvrDevicecfgV40.wDevType) {//目前仅维护可测试设备,补充设备类型查询文档NET_DVR_DEVICECFG_V40的wDevType属性
                        case 2237:
                            deviceLogin.setDVRType(HikConstant.DS_96XXXN_IX);
                            break;
                        case 7504:
                            deviceLogin.setDVRType(HikConstant.IDS_96XX_NX_FA);
                            break;
                        default:
                            deviceLogin.setDVRType(HikConstant.DVR);
                    }
                } else {
                    log.info("错误码: {}", incfg);

                }

            }

            return ConfigJsonUtil.saveOrUpdateDeviceLogin(deviceLogin);
        }
    }

    @Override
    public boolean modifyDeviceInfo(DeviceInfoDTO deviceInfoDTO) {
        return ConfigJsonUtil.saveOrUpdateDeviceInfo(deviceInfoDTO);
    }

    @Override
    public List<DeviceSearchInfoVO> getDeviceList(DeviceSearchInfoVO deviceSearchInfoVo) {
        List<DeviceSearchInfoVO> deviceSearchInfoVOList = new ArrayList<>();
        List<DeviceSearchInfo> deviceSearchInfoList = ConfigJsonUtil.getDeviceSearchInfoList();
        BeanUtil.copyProperties(deviceSearchInfoList, deviceSearchInfoVOList);
        return deviceSearchInfoVOList;
    }

    @Override
    public List<DeviceSearchInfo> getDeviceSearchInfoList(DeviceSearchInfo deviceSearchInfo) {
        return ConfigJsonUtil.getDeviceSearchInfoList();
    }

    @Override
    public DeviceSearchInfo loginStatus(String ip) {
        return ConfigJsonUtil.getDeviceSearchInfoByIp(ip);
    }
}
