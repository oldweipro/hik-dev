package com.oldwei.hikdev.component;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 文件流工具类
 *
 * @author oldwei
 * @date 2021-7-9 10:09
 */
@Component
public class FileStream {
    @Value("${hik-dev.output-dir}")
    private String outputDir;

    /**
     * 创建jpg文件
     *
     * @return 文件绝对路径
     */
    public String touchJpg() {
        return this.touchSuffixFile(".jpg");
    }

    /**
     * 创建json文件
     *
     * @return 文件绝对路径
     */
    public String touchJson() {
        return this.touchSuffixFile(".json");
    }

    /**
     * 创建json文件
     *
     * @return 文件绝对路径
     */
    public String touchPdf() {
        return this.touchSuffixFile(".pdf");
    }

    /**
     * 创建文件
     *
     * @param suffix 后缀名:.json/.pdf/.jpg/...
     * @return 文件绝对路径
     */
    public String touchSuffixFile(String suffix) {
        String filename = System.currentTimeMillis() + suffix;
        String path = System.getProperty("user.dir") + "/" + outputDir + "/" + DateUtil.thisYear() + "/" + (DateUtil.thisMonth() + 1) + "/" + DateUtil.thisDayOfMonth() + "/" + filename;
        FileUtil.touch(path);
        return path;
    }

    /**
     * 下载字节流文件到本地
     *
     * @param pathname 下载地址
     * @param bytes 字节流
     */
    public void downloadToLocal(String pathname, byte[] bytes) {
        try {
            FileOutputStream fos = new FileOutputStream(pathname);
            fos.write(bytes);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
