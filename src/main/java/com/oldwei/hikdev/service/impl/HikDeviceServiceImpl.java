package com.oldwei.hikdev.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.oldwei.hikdev.constant.HikConstant;
import com.oldwei.hikdev.constant.DataCachePrefixConstant;
import com.oldwei.hikdev.entity.Device;
import com.oldwei.hikdev.service.IHikDevService;
import com.oldwei.hikdev.service.IHikDeviceService;
import com.oldwei.hikdev.structure.NET_DVR_DEVICEINFO_V40;
import com.oldwei.hikdev.structure.NET_DVR_USER_LOGIN_INFO;
import com.oldwei.hikdev.util.DataCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author oldwei
 * @date 2021-5-19 13:44
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HikDeviceServiceImpl implements IHikDeviceService {

    private final IHikDevService hikDevService;

    private final DataCache dataCache;

    @Override
    public boolean clean(String ip) {
        Integer longUserId = this.dataCache.getInteger(DataCachePrefixConstant.HIK_REG_USERID_IP + ip);
        //退出的时候注销\释放SDK资源
        if (null != longUserId && longUserId != -1) {
            this.dataCache.set(DataCachePrefixConstant.HIK_REG_USERID_IP + ip, -1);
            return hikDevService.NET_DVR_Logout(longUserId);
        }
//        return true;
        //退出程序时调用注销登录、反初始化接口
        return hikDevService.NET_DVR_Cleanup();
    }

    /**
     * 设备注册
     *
     * @param device
     * @return
     */
    @Override
    public boolean deviceLogin(Device device) {
        // 初始化设备登录信息
        NET_DVR_USER_LOGIN_INFO netDvrUserLoginInfo = new NET_DVR_USER_LOGIN_INFO();
        //设备ip地址
        System.arraycopy(device.getIp().getBytes(), 0, netDvrUserLoginInfo.sDeviceAddress, 0, device.getIp().length());
        //设备用户名
        System.arraycopy(device.getUsername().getBytes(), 0, netDvrUserLoginInfo.sUserName, 0, device.getUsername().length());
        //设备密码
        System.arraycopy(device.getPassword().getBytes(), 0, netDvrUserLoginInfo.sPassword, 0, device.getPassword().length());
        //设备端口
        netDvrUserLoginInfo.wPort = device.getPort();
        //是否异步登录：0- 否，1- 是 默认false
        netDvrUserLoginInfo.bUseAsynLogin = device.getUseAsync();
        netDvrUserLoginInfo.write();

        //设备信息
        NET_DVR_DEVICEINFO_V40 netDvrDeviceInfoV40 = new NET_DVR_DEVICEINFO_V40();
        //用户句柄
        int longUserId = hikDevService.NET_DVR_Login_V40(netDvrUserLoginInfo, netDvrDeviceInfoV40);
        if (longUserId == -1) {
            log.info("登录失败，错误码为:" + hikDevService.NET_DVR_GetLastError());
            return false;
        } else {
            this.dataCache.set(DataCachePrefixConstant.HIK_REG_USERID_IP + device.getIp(), longUserId);
            //设备字符集
            int iCharEncodeType = netDvrDeviceInfoV40.byCharEncodeType;
            this.dataCache.set(DataCachePrefixConstant.HIK_REG_CHAR_ENCODE_TYPE_IP + device.getIp(), iCharEncodeType);
            return true;
        }
    }
}
