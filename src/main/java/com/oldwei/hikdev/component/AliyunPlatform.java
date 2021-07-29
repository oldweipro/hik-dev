package com.oldwei.hikdev.component;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.oldwei.hikdev.entity.StreamAddress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author oldwei
 * @date 2021-7-9 14:21
 */
@Slf4j
@Component
public class AliyunPlatform {
    @Value("${hik-dev.cloud.aliyun.ram.access-key-id}")
    private String accessKeyId;
    @Value("${hik-dev.cloud.aliyun.ram.access-key-secret}")
    private String accessKeySecret;

    @Value("${hik-dev.cloud.aliyun.oss.bucket-name}")
    private String ossBucketName;
    @Value("${hik-dev.cloud.aliyun.oss.endpoint}")
    private String ossEndpoint;
    @Value("${hik-dev.cloud.aliyun.oss.domain}")
    private String ossDomain;

    @Value("${hik-dev.event-push-url}")
    private String eventPushUrl;

    @Value("${hik-dev.cloud.aliyun.live.auth-key}")
    private String authKey;
    @Value("${hik-dev.cloud.aliyun.live.push-stream-domain}")
    private String pushStreamDomain;
    @Value("${hik-dev.cloud.aliyun.live.pull-stream-domain}")
    private String pullStreamDomain;
    @Value("${hik-dev.cloud.aliyun.live.valid-second}")
    private Long validSecond;

    /**
     * 发送文件
     *
     * @param file 文件
     * @return 文件地址
     */
    public String uploadFile(File file) {
        //上传文件
        String uuid = RandomUtil.randomString(32);
        // TODO 可以写活文件路径
        String objectName = "project/smart-theatre/" + DateUtil.thisYear() + "/" + DateUtil.thisMonth() + "/" + DateUtil.thisDayOfMonth() + "/" + uuid + ".jpg";
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(ossEndpoint, accessKeyId, accessKeySecret);
        // 上传Byte数组。
        ossClient.putObject(ossBucketName, objectName, file);
        // 关闭OSSClient。
        ossClient.shutdown();
        return ossDomain + "/" + objectName;
    }

    /**
     * 发送数据到推送地址，restTemplate模拟httpclient发送请求
     *
     * @param data 发送数据
     */
    public void sendDataToCloudApi(JSONObject data) {
        // headers参数
        HttpHeaders requestHeaders = new HttpHeaders();
        // body体参数
        LinkedMultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
        // 设置header是json
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        // 参数设置文件
        requestBody.add("data", data);
        // 封装所有参数
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(requestBody, requestHeaders);
        // 创建RestTemplate
        RestTemplate restTemplate = new RestTemplate();
        // 发起请求
        JSONObject body = restTemplate.postForObject(eventPushUrl, requestEntity, JSONObject.class);
        log.info("上传数据到云端后返回信息：{}", body);
        requestBody.clear();
        requestHeaders.clear();
    }

    /**
     * 获取鉴权后的推流域名
     *
     * @param stream 流名
     * @return 推流地址
     */
    public String getPushStreamDomain(String stream) {
        String rtmp = "rtmp://" + pushStreamDomain + "/live/" + stream;
        return this.authUri(rtmp);
    }

    /**
     * 获取鉴权后的拉流域名
     *
     * @param stream 流名
     * @return 拉流地址列表
     */
    public StreamAddress getPullStreamDomain(String stream) {
        StreamAddress streamAddress = new StreamAddress();
        //rtmp
        String rtmp = "rtmp://" + pullStreamDomain + "/live/" + stream;
        rtmp = this.authUri(rtmp);
        streamAddress.setRtmp(rtmp);
        //flv
        String flv = "https://" + pullStreamDomain + "/live/" + stream + ".flv";
        flv = this.authUri(flv);
        streamAddress.setFlv(flv);
        //hls
        String hls = "https://" + pullStreamDomain + "/live/" + stream + ".m3u8";
        hls = this.authUri(hls);
        streamAddress.setHls(hls);
        return streamAddress;
    }

    /**
     * 地址鉴权
     *
     * @param uri 需要加密的uri地址
     * @return 鉴权地址
     */
    private String authUri(String uri) {
        // 过期时间: 当前时间后的时间戳，秒级
        long exp = System.currentTimeMillis() / 1000 + 3600;
        String pattern = "^(rtmp://|https://)?([^/?]+)(/[^?]*)?(\\\\?.*)?$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(uri);
        String scheme = "", host = "", path = "", args = "";
        if (m.find()) {
            scheme = m.group(1) == null ? "rtmp://" : m.group(1);
            host = m.group(2) == null ? "" : m.group(2);
            path = m.group(3) == null ? "/" : m.group(3);
            args = m.group(4) == null ? "" : m.group(4);
        } else {
            System.out.println("NO MATCH");
        }
        // "0" by default, other value is ok
        String rand = "0";
        // "0" by default, other value is ok
        String uid = "0";
        String sString = String.format("%s-%s-%s-%s-%s", path, exp, rand, uid, authKey);
        String authKey = "";
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(StandardCharsets.UTF_8.encode(sString));
            String hashValue = String.format("%032x", new BigInteger(1, md5.digest()));
            authKey = String.format("%s-%s-%s-%s", exp, rand, uid, hashValue);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (args.isEmpty()) {
            return String.format("%s%s%s%s?auth_key=%s", scheme, host, path, args, authKey);
        } else {
            return String.format("%s%s%s%s&auth_key=%s", scheme, host, path, args, authKey);
        }
    }
}
