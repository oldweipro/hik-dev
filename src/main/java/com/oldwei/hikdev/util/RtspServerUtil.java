package com.oldwei.hikdev.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.system.OsInfo;
import cn.hutool.system.SystemUtil;

/**
 * @author oldwei
 * @date 2022/8/25 18:03
 */
public class RtspServerUtil {
    public static void initRtspServer() {
        OsInfo osInfo = SystemUtil.getOsInfo();
        String rtspServerRootPath = System.getProperty("user.dir") + "/rtsp_server";
        String rtspServerDownloadPath = rtspServerRootPath + "/download";
        String rtspServerDownloadWindowsPath = rtspServerDownloadPath + "/windows_rtsp_server.zip";
        String rtspServerDownloadLinuxPath = rtspServerDownloadPath + "/linux_rtsp_server.zip";
        String urlWindowsPath = "https://oldwei.oss-cn-hangzhou.aliyuncs.com/hik_dev/rtsp_server/rtsp_server_windows.zip";
        String urlLinuxPath = "https://oldwei.oss-cn-hangzhou.aliyuncs.com/hik_dev/rtsp_server/rtsp_server_linux.zip";
        if (osInfo.isWindows()) {
            if (!FileUtil.exist(rtspServerRootPath + "/rtsp_server_windows/config.json") || !FileUtil.exist(rtspServerRootPath + "/rtsp_server_windows/rtsp_server.exe")) {
                downloadRtspServer(rtspServerRootPath, rtspServerDownloadWindowsPath, urlWindowsPath);
            }
            // TODO 启动项目，测试服务是否启动，检测端口号占用，如果占用，找一个没占用的，修改一下配置文件再次运行。
            // 杀死服务
            RuntimeUtil.exec("cmd /c taskkill /f /im rtsp_server.exe");
        } else if (osInfo.isLinux()) {
            if (!FileUtil.exist(rtspServerRootPath + "/rtsp_server_windows/config.json") || !FileUtil.exist(rtspServerRootPath + "/rtsp_server_windows/rtsp_server")) {
                downloadRtspServer(rtspServerRootPath, rtspServerDownloadLinuxPath, urlLinuxPath);
            }
            // TODO 启动项目，测试服务是否启动，检测端口号占用，如果占用，找一个没占用的，修改一下配置文件再次运行。
            RuntimeUtil.exec(rtspServerRootPath + "/rtsp_server_windows/rtsp_server");
        }
    }

    private static void downloadRtspServer(String rtspServerRootPath, String rtspServerDownloadWindowsPath, String urlPath) {
        try {
            // 从远端拉取sdk解压到本地
            HttpUtil.downloadFile(urlPath, FileUtil.file(rtspServerDownloadWindowsPath));
        } catch (Exception e) {
            System.out.println("网络异常，下载rtsp server文件失败。");
            System.exit(0);
        }
        try {
            // 解压下载的rtsp server文件到rtsp_server根目录
            ZipUtil.unzip(rtspServerDownloadWindowsPath, rtspServerRootPath);
        } catch (Exception e) {
            System.out.println("rtsp_server文件解压异常");
            System.exit(0);
        }
    }
}
