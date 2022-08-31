package com.oldwei.hikdev.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.oldwei.hikdev.entity.config.DeviceChannel;
import com.oldwei.hikdev.entity.config.DeviceSearchInfo;
import com.oldwei.hikdev.entity.rtsp.RtspStreams;
import com.oldwei.hikdev.service.IRtspServerService;
import com.oldwei.hikdev.util.ConfigJsonUtil;
import com.oldwei.hikdev.util.RtspServerHttpUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RtspServerServiceImpl implements IRtspServerService {
    @Override
    public void syncRtspStream() {
        List<DeviceSearchInfo> deviceSearchInfoList = ConfigJsonUtil.getDeviceSearchInfoList();
        for (DeviceSearchInfo d :
                deviceSearchInfoList) {
            List<DeviceChannel> deviceChannels = d.getDeviceChannels();
            if (deviceChannels.size() > 0) {
                RtspStreams rtspStreams = new RtspStreams();
                Map<String, Object> channels = MapUtil.newHashMap();
                for (int i = 0; i < deviceChannels.size(); i++) {
                    DeviceChannel dc = deviceChannels.get(i);
                    if (dc.getByEnable() > 0) {
                        Map<String, Object> rtsp = MapUtil.newHashMap();
                        rtsp.put("url", dc.getRtspStream());
                        channels.put(String.valueOf(i), rtsp);
                    }
                }
                rtspStreams.setChannels(channels);
                rtspStreams.setName(StrUtil.isBlankIfStr(d.getTitle()) ? d.getIpv4Address() : d.getTitle());
                // 添加流信息
                String edit = RtspServerHttpUtil.post("stream/" + d.getDeviceSn() + "/edit", JSON.toJSONString(rtspStreams));
                String add = RtspServerHttpUtil.post("stream/" + d.getDeviceSn() + "/add", JSON.toJSONString(rtspStreams));
            }
        }
    }
}
