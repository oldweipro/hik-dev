package com.oldwei.hikdev.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.oldwei.hikdev.entity.device.DeviceLogin;
import com.oldwei.hikdev.entity.device.DeviceSearchInfoVo;
import com.oldwei.hikdev.service.IHikDevService;
import com.oldwei.hikdev.service.IHikDeviceService;
import com.oldwei.hikdev.structure.NET_DVR_DEVICEINFO_V40;
import com.oldwei.hikdev.structure.NET_DVR_USER_LOGIN_INFO;
import com.oldwei.hikdev.component.DataCache;
import com.oldwei.hikdev.util.ConfigJsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
        DeviceLogin deviceLoginByIp = ConfigJsonUtil.getDeviceLoginByIp(ip);
        if (ObjectUtil.isNotNull(deviceLoginByIp)) {
            Integer longUserId = deviceLoginByIp.getLoginId();
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
    public boolean login(DeviceLogin deviceLogin) {
        // 初始化设备登录信息
        NET_DVR_USER_LOGIN_INFO netDvrUserLoginInfo = new NET_DVR_USER_LOGIN_INFO();
        //设备ip地址
        System.arraycopy(deviceLogin.getIp().getBytes(), 0, netDvrUserLoginInfo.sDeviceAddress, 0, deviceLogin.getIp().length());
        //设备用户名
        System.arraycopy(deviceLogin.getUsername().getBytes(), 0, netDvrUserLoginInfo.sUserName, 0, deviceLogin.getUsername().length());
        //设备密码
        System.arraycopy(deviceLogin.getPassword().getBytes(), 0, netDvrUserLoginInfo.sPassword, 0, deviceLogin.getPassword().length());
        //设备端口
        netDvrUserLoginInfo.wPort = deviceLogin.getPort();
        //是否异步登录：0- 否，1- 是 默认false
        netDvrUserLoginInfo.bUseAsynLogin = deviceLogin.getUseAsync();
        netDvrUserLoginInfo.write();

        //设备信息
        NET_DVR_DEVICEINFO_V40 netDvrDeviceInfoV40 = new NET_DVR_DEVICEINFO_V40();
        //用户句柄
        int longUserId = hikDevService.NET_DVR_Login_V40(netDvrUserLoginInfo, netDvrDeviceInfoV40);
        if (longUserId == -1) {
            log.info("登录失败，错误码为:" + hikDevService.NET_DVR_GetLastError());
            return false;
        } else {
            deviceLogin.setLoginId(longUserId);
            //设备字符集
            int iCharEncodeType = netDvrDeviceInfoV40.byCharEncodeType;
            deviceLogin.setCharEncodeType(iCharEncodeType);
            return ConfigJsonUtil.saveOrUpdateDeviceLogin(deviceLogin);
        }
    }

    @Override
    public List<DeviceSearchInfoVo> getDeviceList(DeviceSearchInfoVo deviceSearchInfoVo) {
        return ConfigJsonUtil.readConfigJson().getJSONArray("deviceSearch").toList(DeviceSearchInfoVo.class);
    }

    @Override
    public DeviceLogin loginStatus(String ip) {
        return ConfigJsonUtil.getDeviceLoginByIp(ip);
    }

}
