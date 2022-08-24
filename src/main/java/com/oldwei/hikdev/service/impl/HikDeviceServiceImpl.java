package com.oldwei.hikdev.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.oldwei.hikdev.entity.config.DeviceLoginDTO;
import com.oldwei.hikdev.entity.config.DeviceSearchInfo;
import com.oldwei.hikdev.entity.config.DeviceSearchInfoVO;
import com.oldwei.hikdev.service.FLoginResultCallBack;
import com.oldwei.hikdev.service.IHikAlarmDataService;
import com.oldwei.hikdev.service.IHikDevService;
import com.oldwei.hikdev.service.IHikDeviceService;
import com.oldwei.hikdev.structure.NET_DVR_DEVICEINFO_V30;
import com.oldwei.hikdev.structure.NET_DVR_DEVICEINFO_V40;
import com.oldwei.hikdev.structure.NET_DVR_USER_LOGIN_INFO;
import com.oldwei.hikdev.util.ConfigJsonUtil;
import com.sun.jna.Pointer;
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
            if (lpDeviceinfo.byChanNum > 0) {
                List<Integer> analogIds = new ArrayList<>();
                for (int i = 0; i < lpDeviceinfo.byStartChan; i++) {
                    analogIds.add(lpDeviceinfo.byStartChan + i);
                }
                allIds.addAll(analogIds);
                deviceLogin.setAnalogChannelIds(analogIds);
            }

            if (ipChan > 0) {
                List<Integer> digitalIds = new ArrayList<>();
                for (int i = 0; i < ipChan; i++) {
                    digitalIds.add(lpDeviceinfo.byStartDChan + i);
                }
                allIds.addAll(digitalIds);
                deviceLogin.setDigitalChannelIds(digitalIds);
            }
            deviceLogin.setAllChannelIds(allIds);
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
            return ConfigJsonUtil.saveOrUpdateDeviceLogin(deviceLogin);
        }
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
