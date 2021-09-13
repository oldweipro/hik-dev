package com.oldwei.hikdev.service.impl;

import cn.hutool.core.util.XmlUtil;
import com.oldwei.hikdev.service.IUdpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author oldwei
 * @date 2021-9-13 9:29
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UdpServiceImpl implements IUdpService {
    private final DatagramSocket datagramSocket;

    @Override
    @Async("asyncServiceExecutor")
    public void receive() {
        DatagramPacket packet;
        while (true) {
            byte[] buffer = new byte[4096];
            packet = new DatagramPacket(buffer, buffer.length);
            try {
                datagramSocket.receive(packet);
                // 接收到的UDP信息，然后解码
                byte[] bufferData = packet.getData();
                String decodeData = new String(bufferData, StandardCharsets.UTF_8).trim();
                Map<String, Object> map = XmlUtil.xmlToMap(decodeData);
//                ProbeMatch probeMatch = BeanUtil.fillBeanWithMap(map, new ProbeMatch(), false);
                log.info("=======Process decodeData UTF-8======{}", map);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
