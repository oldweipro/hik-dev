package com.oldwei.hikdev.sdk.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.oldwei.hikdev.sdk.constant.HikConstant;
import com.oldwei.hikdev.sdk.constant.RedisPrefixConstant;
import com.oldwei.hikdev.sdk.service.IHikDevService;
import com.oldwei.hikdev.sdk.service.IHikDeviceService;
import com.oldwei.hikdev.sdk.structure.NET_DVR_DEVICEINFO_V40;
import com.oldwei.hikdev.sdk.structure.NET_DVR_USER_LOGIN_INFO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * @author oldwei
 * @date 2021-5-19 13:44
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HikDeviceServiceImpl implements IHikDeviceService {

    private final IHikDevService hikDevService;

    private final RedisTemplate<String, Serializable> redisTemplate;

    @Override
    public boolean clean(String ip) {
        Integer longUserId = (Integer) this.redisTemplate.opsForValue().get(RedisPrefixConstant.HIK_REG_USERID_IP + ip);
        //退出的时候注销\释放SDK资源
        if (null != longUserId) {
            return hikDevService.NET_DVR_Logout(longUserId);
        }
        return true;
        //退出程序时调用注销登录、反初始化接口
//        return hikDevService.NET_DVR_Cleanup();
    }

    /**
     * 设备注册
     *
     * @param jsonObject
     * @return
     */
    @Override
    public boolean deviceLogin(JSONObject jsonObject) {
        String ip = jsonObject.getString("ip");
        String username = jsonObject.getString("username");
        String password = jsonObject.getString("password");
        Short port = jsonObject.getShort("port");
        boolean useAsync = false;
        // 设备登录信息
        NET_DVR_USER_LOGIN_INFO netDvrUserLoginInfo = new NET_DVR_USER_LOGIN_INFO();

        //设备ip地址
        netDvrUserLoginInfo.sDeviceAddress = new byte[HikConstant.NET_DVR_DEV_ADDRESS_MAX_LEN];
        System.arraycopy(ip.getBytes(), 0, netDvrUserLoginInfo.sDeviceAddress, 0, ip.length());

        //设备用户名
        netDvrUserLoginInfo.sUserName = new byte[HikConstant.NET_DVR_LOGIN_USERNAME_MAX_LEN];
        System.arraycopy(username.getBytes(), 0, netDvrUserLoginInfo.sUserName, 0, username.length());

        //设备密码
        netDvrUserLoginInfo.sPassword = new byte[HikConstant.NET_DVR_LOGIN_PASSWD_MAX_LEN];
        System.arraycopy(password.getBytes(), 0, netDvrUserLoginInfo.sPassword, 0, password.length());

        netDvrUserLoginInfo.wPort = port;
        //是否异步登录：0- 否，1- 是 默认false
        netDvrUserLoginInfo.bUseAsynLogin = useAsync;
        netDvrUserLoginInfo.write();

        //设备信息
        NET_DVR_DEVICEINFO_V40 netDvrDeviceInfoV40 = new NET_DVR_DEVICEINFO_V40();
        //用户句柄
        int longUserId = hikDevService.NET_DVR_Login_V40(netDvrUserLoginInfo, netDvrDeviceInfoV40);
        if (longUserId == -1) {
            log.info("登录失败，错误码为:" + hikDevService.NET_DVR_GetLastError());
            return false;
        } else {
            this.redisTemplate.opsForValue().set(RedisPrefixConstant.HIK_REG_USERID_IP + ip, longUserId);
            //设备字符集
            int iCharEncodeType = netDvrDeviceInfoV40.byCharEncodeType;
            this.redisTemplate.opsForValue().set(RedisPrefixConstant.HIK_REG_CHAR_ENCODE_TYPE_IP + ip, iCharEncodeType);
            return true;
        }
    }
}
