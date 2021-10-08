package com.oldwei.hikdev.component;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.setting.Setting;
import com.alibaba.fastjson.JSONObject;
import com.oldwei.hikdev.entity.ConfigSettingBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author oldwei
 * @date 2021-9-23 15:11
 */
@Slf4j
@Component
public class UdpDatagramSocket {
    private DatagramSocket datagramSocket;
    private final Setting configSetting;

    public UdpDatagramSocket(Setting configSetting) {
        this.configSetting = configSetting;
        this.setDatagramSocket();
        this.setUdpListener();
    }

    public DatagramSocket getDatagramSocket() {
        return this.datagramSocket;
    }

    private void setDatagramSocket() {
        try {
            this.datagramSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
    @SuppressWarnings("InfiniteLoopStatement")
    private void setUdpListener() {
        ThreadUtil.execAsync(() -> {
            DatagramPacket packet;
            while (true) {
                byte[] buffer = new byte[4096];
                packet = new DatagramPacket(buffer, buffer.length);
                this.datagramSocket.receive(packet);
                // 接收到的UDP信息，然后解码
                byte[] bufferData = packet.getData();
                String decodeData = new String(bufferData, StandardCharsets.UTF_8).trim();
                Map<String, Object> map = XmlUtil.xmlToMap(decodeData);
                ConfigSettingBean configSettingBean = new ConfigSettingBean();
                this.configSetting.toBean(configSettingBean);
                map.put("projectId", configSettingBean.getProjectId());
                map.put("tenantId", configSettingBean.getTenantId());
                ThreadUtil.execAsync(() -> {
                    HttpUtil.post(configSettingBean.getUploadDataUrl(), JSONObject.toJSONString(map));
                });
            }
        });
    }
}
