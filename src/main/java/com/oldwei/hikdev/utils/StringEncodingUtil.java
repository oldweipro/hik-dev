package com.oldwei.hikdev.utils;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * @author oldwei
 * @date 2021-5-14 17:28
 */
@Slf4j
public class StringEncodingUtil {
    /**
     * 判断编码格式，并返回原字符串
     *
     * @param bytes 字节数组
     * @return
     */
    public static String guessEncodingTransformString(byte[] bytes) {
        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(bytes, 0, bytes.length);
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        detector.reset();
        String strName = "";
        if (StrUtil.isNotBlank(encoding) && StrUtil.equals("UTF-8", encoding)) {
            strName = new String(bytes, StandardCharsets.UTF_8).trim();
        } else {
            try {
                strName = new String(bytes, "GBK").trim();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                log.error("转换GBK字符串异常");
            }
        }
        return strName;
    }
}
