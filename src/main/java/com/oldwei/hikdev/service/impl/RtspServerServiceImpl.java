package com.oldwei.hikdev.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.oldwei.hikdev.entity.config.DeviceChannel;
import com.oldwei.hikdev.entity.config.DeviceSearchInfo;
import com.oldwei.hikdev.entity.rtsp.RtspStreams;
import com.oldwei.hikdev.service.IRtspServerService;
import com.oldwei.hikdev.util.ConfigJsonUtil;
import com.oldwei.hikdev.util.RtspServerHttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class RtspServerServiceImpl implements IRtspServerService {
    @Override
    public void syncRtspStream() {
        List<DeviceSearchInfo> deviceSearchInfoList = ConfigJsonUtil.getDeviceSearchInfoList();
        for (int i = 0; i < deviceSearchInfoList.size(); i++) {
            DeviceSearchInfo d = deviceSearchInfoList.get(i);
            List<DeviceChannel> deviceChannels = d.getDeviceChannels();
            if (deviceChannels.size() > 0) {
                RtspStreams rtspStreams = new RtspStreams();
                Map<String, Object> channels = new ConcurrentHashMap<>();
                for (int j = 0; j < deviceChannels.size(); j++) {
                    DeviceChannel dc = deviceChannels.get(j);
                    if (dc.getByEnable() > 0) {
                        Map<String, Object> rtsp = new ConcurrentHashMap<>();
                        rtsp.put("url", dc.getRtspStream());
                        rtsp.put("debug", false);
                        rtsp.put("on_demand", false);
                        channels.put(String.valueOf(dc.getChannelId()), rtsp);
                    }
                }
                rtspStreams.setChannels(channels);
                rtspStreams.setName(StrUtil.isBlankIfStr(d.getTitle()) ? d.getIpv4Address() : d.getTitle());
                // 提高兼容性,uuid长度大于32位会接口卡死，使用deviceId(由deviceSn的16位MD5值)
                String uuid = d.getDeviceId();
                rtspStreams.setUuid(uuid);
                // 添加流信息
                String edit = RtspServerHttpUtil.post("stream/" + uuid + "/edit", JSON.toJSONString(rtspStreams));
                String add = RtspServerHttpUtil.post("stream/" + uuid + "/add", JSON.toJSONString(rtspStreams));
            }
        }
    }
}
