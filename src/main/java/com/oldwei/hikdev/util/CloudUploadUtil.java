package com.oldwei.hikdev.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;

/**
 * 阿里云对象存储上传工具类
 *
 * @author oldwei
 * @date 2021-5-18 18:07
 */
@Slf4j
public class CloudUploadUtil {

    /**
     * 以下信息在阿里云控制台查找
     */
    private static final String ACCESS_KEY_ID = "accessKeyId";
    private static final String ACCESS_KEY_SECRET = "accessKeySecret";
    private static final String BUCKET_NAME = "your bucket name";
    private static final String ENDPOINT = "https://your.endpoint";
    private static final String DOMAIN = "https://your.domain";
    private static final String CLOUD_URL = "https://推送地址";

    public static String uploadFile(File file) {
        //上传文件
        String uuid = RandomUtil.randomString(32);
        // 文件路径
        String objectName = "project/smart-theatre/" + DateUtil.today() + "/" + uuid + ".jpg";
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
        // 上传Byte数组。
        ossClient.putObject(BUCKET_NAME, objectName, file);
        // 关闭OSSClient。
        ossClient.shutdown();
        return DOMAIN + objectName;
    }

    /**
     * 发送数据到云端接口
     *
     * @param data 数据
     */
    public static void sendDataToCloudApi(JSONObject data) {
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
        JSONObject body = restTemplate.postForObject(CLOUD_URL, requestEntity, JSONObject.class);
        log.info("上传数据到云端后返回信息：{}", body);
        requestBody.clear();
        requestHeaders.clear();
    }
}
