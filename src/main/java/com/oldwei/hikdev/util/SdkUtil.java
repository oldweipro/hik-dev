package com.oldwei.hikdev.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.system.OsInfo;
import cn.hutool.system.SystemUtil;

/**
 * @author oldwei
 * @date 2022/8/24 17:11
 */
public class SdkUtil {

    public static void initSdk() {
        OsInfo osInfo = SystemUtil.getOsInfo();
        String sdkRootPath = System.getProperty("user.dir") + "/sdk";
        String sdkDownloadPath = sdkRootPath + "/download";
        String sdkDownloadWindowsPath = sdkDownloadPath + "/windows.zip";
        String sdkDownloadLinuxPath = sdkDownloadPath + "/linux.zip";
        String urlWindowsPath = "https://oldwei.oss-cn-hangzhou.aliyuncs.com/hik_dev/sdk/windows.zip";
        String urlLinuxPath = "https://oldwei.oss-cn-hangzhou.aliyuncs.com/hik_dev/sdk/linux.zip";
        if (osInfo.isWindows()) {
            boolean exist = FileUtil.exist(System.getProperty("user.dir") + "/sdk/windows/HCNetSDK.dll");
            if (!exist) {
                downloadSdk(sdkRootPath, sdkDownloadWindowsPath, urlWindowsPath);
            }
        } else if (osInfo.isLinux()) {
            boolean exist = FileUtil.exist(System.getProperty("user.dir") + "/sdk/linux/libhcnetsdk.so");
            if (!exist) {
                downloadSdk(sdkRootPath, sdkDownloadLinuxPath, urlLinuxPath);
            }
        }
    }
    private static void downloadSdk(String sdkRootPath, String sdkDownloadWindowsPath, String urlWindowsPath) {
        try {
            // 从远端拉取sdk解压到本地
            HttpUtil.downloadFile(urlWindowsPath, FileUtil.file(sdkDownloadWindowsPath));
        } catch (Exception e) {
            System.out.println("网络异常，下载SDK文件失败。");
            System.exit(0);
        }
        try {
            // 解压下载的WindowsSDK文件到sdk根目录
            ZipUtil.unzip(sdkDownloadWindowsPath, sdkRootPath);
        } catch (Exception e) {
            System.out.println("SDK文件解压异常");
            System.exit(0);
        }
    }

}
