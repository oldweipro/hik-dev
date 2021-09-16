package com.oldwei.hikdev.runner;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author oldwei
 * @date 2021-9-16 12:27
 */
@Slf4j
@WebListener
public class UdpListener implements ServletContextListener {
    public static DatagramSocket datagramSocket;

    /**
     * udp监听端口
     */
    public static Integer updPort;
    /**
     * 数据上传地址
     */
    public static String uploadDataUrl;
    static {
        // 获取sdk/config文件夹下的配置文件
        String property = System.getProperty("user.dir") + "\\sdk\\config\\config.json";
        FileReader fileReader = new FileReader(property);
        // 获取到文件中的字符串
        String result = fileReader.readString();
        // 格式化为json对象
        JSONObject jsonObject = JSONObject.parseObject(result);
        log.info("格式化的json对象：{}", jsonObject);
        // udp监听端口
        updPort = jsonObject.getInteger("updPort");
        // 数据上传的地址
        uploadDataUrl = jsonObject.getString("uploadDataUrl");
        try {
            datagramSocket = new DatagramSocket(updPort);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("=============UDPListener============");
        ThreadUtil.execAsync(() -> {
            DatagramPacket packet;
            while (true) {
                byte[] buffer = new byte[4096];
                packet = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(packet);
                // 接收到的UDP信息，然后解码
                byte[] bufferData = packet.getData();
                String decodeData = new String(bufferData, StandardCharsets.UTF_8).trim();
                Map<String, Object> map = XmlUtil.xmlToMap(decodeData);
                HttpUtil.post(uploadDataUrl, JSONObject.toJSONString(map));
                log.info("=======Process decodeData UTF-8======{}", map);
            }
        });
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("================Destroyed=============");
    }
}
