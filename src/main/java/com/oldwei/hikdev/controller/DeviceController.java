package com.oldwei.hikdev.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.PageUtil;
import com.oldwei.hikdev.annotation.CheckDeviceLogin;
import com.oldwei.hikdev.component.FileStream;
import com.oldwei.hikdev.constant.HikConstant;
import com.oldwei.hikdev.entity.QueryRequest;
import com.oldwei.hikdev.entity.config.DeviceLoginDTO;
import com.oldwei.hikdev.entity.HikDevResponse;
import com.oldwei.hikdev.entity.config.DeviceSearchInfo;
import com.oldwei.hikdev.service.IHikAlarmDataService;
import com.oldwei.hikdev.service.IHikCameraService;
import com.oldwei.hikdev.service.IHikDevService;
import com.oldwei.hikdev.service.IHikDeviceService;
import com.oldwei.hikdev.structure.*;
import com.oldwei.hikdev.util.ConfigJsonUtil;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author oldwei
 * @date 2021-7-7 14:37
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("api/device")
public class DeviceController {

    private final IHikDeviceService hikDeviceService;
    private final IHikCameraService hikCameraService;
    private final IHikAlarmDataService hikAlarmDataService;
    private final IHikDevService hikDevService;
    private final FileStream fileStream;

    @Value("${hik-dev.output-dir}")
    private String outputDir;

    /**
     * 设备注册登录
     *
     * @param deviceLogin 设备基本信息IP、username、password、port
     * @return 登录结果 true/false
     */
    @PostMapping("login")
    public HikDevResponse login(@Valid @RequestBody DeviceLoginDTO deviceLogin) {
        return this.hikDeviceService.login(deviceLogin) ? new HikDevResponse().ok().msg("注册成功") : new HikDevResponse().err().msg("注册失败");
    }

    /**
     * 根据设备IP获取设备详细信息
     * 登录状态
     * 布防状态
     *
     * @param ipv4Address 设备ip
     * @return 登录结果 true/false
     */
    @GetMapping("getDeviceInfoByIp/{ipv4Address}")
    public HikDevResponse getDeviceInfoByIp(@PathVariable String ipv4Address) {
        DeviceSearchInfo deviceLogin = this.hikDeviceService.loginStatus(ipv4Address);
        return new HikDevResponse().ok().data(deviceLogin).msg("查询成功");
    }

    /**
     * 设备注销退出
     *
     * @param ipv4Address 设备ip
     * @return 注销结果 true/false
     */
    @CheckDeviceLogin
    @PostMapping("clean/{ipv4Address}")
    public HikDevResponse clean(@PathVariable String ipv4Address) {
        return this.hikDeviceService.clean(ipv4Address) ? new HikDevResponse().ok().msg("注销成功") : new HikDevResponse().err().msg("注销失败");
    }

    /**
     * 获取设备列表
     *
     * @param deviceSearchInfo
     * @return
     */
    @GetMapping("getDeviceSearchInfoList")
    public HikDevResponse getDeviceSearchInfoList(QueryRequest queryRequest, DeviceSearchInfo deviceSearchInfo) {
        int pageNum = queryRequest.getPageNum();
        int pageSize = queryRequest.getPageSize();

        List<DeviceSearchInfo> deviceSearchInfoList = this.hikDeviceService.getDeviceSearchInfoList(deviceSearchInfo);
        int counts = deviceSearchInfoList.size();
        List<DeviceSearchInfo> page = ListUtil.page(pageNum - 1, pageSize, deviceSearchInfoList);
        Map<String, Object> map = MapUtil.newConcurrentHashMap();
        map.put("counts", counts);
        map.put("pagesize", pageSize);
        map.put("pages", PageUtil.totalPage(counts, pageSize));
        map.put("page", pageNum);
        map.put("items", page);
        return new HikDevResponse().ok().data(map).msg("查询成功");
    }

    /**
     * 主动同步（扫描）局域网设备
     */
    @GetMapping("searchHikDevice")
    public HikDevResponse searchHikDevice() {
        ConfigJsonUtil.searchHikDevice();
        return new HikDevResponse().ok().msg("扫描中");
    }

    /**
     * 设备布防
     *
     * @param ipv4Address 设备IP
     */
    @CheckDeviceLogin
    @PostMapping("setupAlarm/{ipv4Address}")
    public HikDevResponse setupAlarm(@PathVariable String ipv4Address) {
        return this.hikAlarmDataService.setupAlarmChan(ipv4Address);
    }

    /**
     * 设备撤防
     *
     * @param ipv4Address 设备ip
     */
    @CheckDeviceLogin
    @PostMapping("closeAlarm/{ipv4Address}")
    public HikDevResponse closeAlarm(@PathVariable String ipv4Address) {
        return this.hikAlarmDataService.closeAlarmChan(ipv4Address);
    }

    /**
     * 抓取当前照片到内存
     *
     * @param ipv4Address 设备IP
     * @param picQuality  照片质量
     * @param picSize     照片分辨率（尺寸）
     * @return base64
     */
    @CheckDeviceLogin
    @GetMapping("captureJpegPictureToMemory/{ipv4Address}")
    public HikDevResponse captureJpegPictureToMemory(@PathVariable String ipv4Address, Integer channelId, Short picQuality, Short picSize) {
        DeviceSearchInfo deviceSearchInfoByIp = ConfigJsonUtil.getDeviceSearchInfoByIp(ipv4Address);
        // 这里两个参数经过测试,可能设备比较老wPicSize:只支持0=CIF，wPicQuality：0-最好：图片质量大小220K，1-较好：大小70K，2-一般：大小40K
        NET_DVR_JPEGPARA netDvrJpegpara = new NET_DVR_JPEGPARA();
        if (ObjectUtil.isNotNull(picQuality)) {
            log.info("picQuality: {}", picQuality);
            netDvrJpegpara.wPicQuality = picQuality;
        }
        if (ObjectUtil.isNotNull(picSize)) {
            log.info("picSize: {}", picSize);
            netDvrJpegpara.wPicSize = picSize;
        }
        // 如果设置小了，会报错误43：缓冲区太小。接收设备数据的缓冲区或存放图片缓冲区不足。
        // 我大概看了一下门禁抓拍的照片普遍在40-120K左右，可能是由于画面颜色单一照片质量较小，这里设置了1024KB，开发者可根据实际情况修改缓冲区大小
        byte[] sJpegPicBuffer = new byte[1024000];
        IntByReference lpSizeReturned = new IntByReference(sJpegPicBuffer.length);
        log.info("=====================查看通道号 {}====================", channelId);
        if (channelId == null) {
            channelId = 1;
        }
        boolean b = this.hikDevService.NET_DVR_CaptureJPEGPicture_NEW(deviceSearchInfoByIp.getLoginId(), channelId, netDvrJpegpara, sJpegPicBuffer, sJpegPicBuffer.length, lpSizeReturned);
        // 抓完图后可以直接使用sJpegPicBuffer，由于大小为204800所以存储的照片大小为200k，所以如果使用NET_DVR_CaptureJPEGPicture_NEW存储照片的话，得转一下
        byte[] savePic = new byte[lpSizeReturned.getValue()];
        System.arraycopy(sJpegPicBuffer, 0, savePic, 0, savePic.length);
        String encode = Base64.encode(savePic);
        int error = this.hikDevService.NET_DVR_GetLastError();
        if (error == 0) {
            return new HikDevResponse().ok("截图成功").data(encode);
        } else {
            log.info("errorCode: {}", error);
            return new HikDevResponse().err("截图失败: " + error).data(error);
        }
    }

    /**
     * 抓取当前照片到本地
     *
     * @param ipv4Address 设备ip
     */
    @CheckDeviceLogin
    @GetMapping("captureJpegPictureToLocal/{ipv4Address}")
    public HikDevResponse captureJpegPictureToLocal(@PathVariable String ipv4Address, Integer channelId, Short picQuality, Short picSize) {
        // 这里两个参数经过测试,可能设备比较老wPicSize:只支持0=CIF，wPicQuality：0-最好：图片质量大小220K，1-较好：大小70K，2-一般：大小40K
        NET_DVR_JPEGPARA netDvrJpegpara = new NET_DVR_JPEGPARA();
        if (ObjectUtil.isNotNull(picQuality)) {
            log.info("picQuality: {}", picQuality);
            netDvrJpegpara.wPicQuality = picQuality;
        }
        if (ObjectUtil.isNotNull(picSize)) {
            log.info("picSize: {}", picSize);
            netDvrJpegpara.wPicSize = picSize;
        }
        String touchJpg = this.fileStream.touchJpg();
        DeviceSearchInfo deviceSearchInfoByIp = ConfigJsonUtil.getDeviceSearchInfoByIp(ipv4Address);
        log.info("=====================查看通道号 {}====================", channelId);
        if (channelId == null) {
            channelId = 1;
        }
        // 如果要存储照片到本地，建议使用NET_DVR_CaptureJPEGPicture
        this.hikDevService.NET_DVR_CaptureJPEGPicture(
                deviceSearchInfoByIp.getLoginId(),
                channelId,
                netDvrJpegpara,
                touchJpg.getBytes());
        int error = this.hikDevService.NET_DVR_GetLastError();
        DeviceLoginDTO deviceLoginDTO = deviceSearchInfoByIp.findDeviceLoginDTO();
        String urlPath = touchJpg.split(outputDir)[1];
        log.info("分割之后的地址: {}", urlPath);
        deviceLoginDTO.setScreenPicture(urlPath);
        ConfigJsonUtil.saveOrUpdateDeviceLogin(deviceLoginDTO);
        if (error == 0) {
            return new HikDevResponse().ok("截图成功").data(urlPath);
        } else {
            log.info("errorCode: {}", error);
            return new HikDevResponse().err("截图失败: " + error).data(error);
        }
    }

    /**
     * 开启预览
     *
     * @param ipv4Address 设备ip
     */
    @CheckDeviceLogin
    @GetMapping("openPreview/{ipv4Address}")
    public HikDevResponse openPreview(@PathVariable String ipv4Address) {
        Integer loginId = ConfigJsonUtil.getDeviceSearchInfoByIp(ipv4Address).getLoginId();
        this.hikCameraService.openPreview(loginId, ipv4Address);
        return new HikDevResponse().ok("门已打开");
    }

    // TODO 关闭预览

    /**
     * [需要开启预览]使用sdk存储视频录像到本地，每一个小时自动创建新文件
     *
     * @param ipv4Address 设备IP
     * @return
     */
    @PostMapping("startCameraRecord/{ipv4Address}")
    public String saveCameraData(@PathVariable String ipv4Address) {
        Integer previewSucValue = ConfigJsonUtil.getDeviceSearchInfoByIp(ipv4Address).getPreviewHandleId();
        if (null == previewSucValue || previewSucValue == -1) {
            log.error("设备未开启预览");
            return "设备未开启预览";
        }
        this.hikCameraService.saveCameraData(previewSucValue);
        return "启动成功！";
    }

    /**
     * [需要开启预览]停止设备录像
     *
     * @param ipv4Address 设备IP
     * @return
     */
    @PostMapping("stopCameraRecord/{ipv4Address}")
    public String stopCameraData(@PathVariable String ipv4Address) {
        Integer previewSucValue = ConfigJsonUtil.getDeviceSearchInfoByIp(ipv4Address).getPreviewHandleId();
        if (null == previewSucValue || previewSucValue == -1) {
            log.error("设备未开启预览");
            return "设备未开启预览";
        }
        // TODO 停止设备录像
//        this.hikCameraService.stopCameraData(previewSucValue);
        return "停止成功！";
    }

}
