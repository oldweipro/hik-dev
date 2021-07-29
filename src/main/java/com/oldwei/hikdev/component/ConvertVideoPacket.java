package com.oldwei.hikdev.component;

import com.oldwei.hikdev.constant.DataCachePrefixConstant;
import com.oldwei.hikdev.constant.VideoConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.IplImage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PipedInputStream;
import java.time.LocalDateTime;

import static org.bytedeco.ffmpeg.global.avcodec.av_packet_unref;

/**
 * rtsp转rtmp（转封装方式）
 *
 * @author oldwei
 * @date 2021-7-12 22:07
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class ConvertVideoPacket {
    private final DataCache dataCache;
    private final FileStream fileStream;

    /**
     * 选择视频源
     *
     * @param pis sdk码流数据
     * @param out 输出地址/推流地址
     * @param ip  推流设备IP地址/推流ID
     * @throws IOException 推流失败
     */
    public void fromPis(PipedInputStream pis, String out, String ip) throws IOException {
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(pis, 0);
        this.setGrabber(grabber, out, ip);
    }

    /**
     * 选择视频源
     *
     * @param rtspUrl rtsp流地址
     * @param out     输出地址/推流地址
     * @param ip      推流设备IP地址/推流ID
     * @throws IOException 推流失败
     */
    public void fromRtsp(String rtspUrl, String out, String ip) throws IOException {
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(rtspUrl);
        if (rtspUrl.contains(VideoConstant.RTSP)) {
            grabber.setOption("rtsp_transport", "tcp");
        }
        this.setGrabber(grabber, out, ip);
    }

    /**
     * 设置流抓取器
     *
     * @param grabber rtsp流地址
     * @param out     输出地址/推流地址
     * @param ip      推流设备IP地址/推流ID
     * @throws IOException 推流失败
     */
    private void setGrabber(FFmpegFrameGrabber grabber, String out, String ip) throws IOException {
        // 开始之后ffmpeg会采集视频信息，之后就可以获取音视频信息
        grabber.start();
        //一个opencv视频帧转换器
        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
        int i = 0;
        Frame frame = null;
        int len = 10;
        while (i < len) {
            frame = grabber.grabFrame();
            if (frame.image != null) {
                log.info("取到第一帧：" + i);
                break;
            }
            i++;
        }
        IplImage iplImage = converter.convert(frame);
        //保存第一帧图片
        String touchJpg = this.fileStream.touchJpg();
        opencv_imgcodecs.cvSaveImage(touchJpg, iplImage);
        int width = grabber.getImageWidth();
        int height = grabber.getImageHeight();
        // 视频参数 视频编码ID
        int videoCodecId = grabber.getVideoCodec();
        log.info("视频编码ID:{}", videoCodecId);
        // 帧率
        double frameRate = grabber.getVideoFrameRate();
        //比特率
        int bitRate = grabber.getVideoBitrate();
        // 音频参数 音频编码ID
        int audioCodecId = grabber.getAudioCodec();
        log.info("音频编码ID:{}", audioCodecId);
        // 想要录制音频，这三个参数必须有：audioChannels > 0 && audioBitrate > 0 && sampleRate > 0
        int audioChannels = grabber.getAudioChannels();
        log.info("音频通道:{}", audioChannels);
        int audioBitRate = grabber.getAudioBitrate();
        log.info("音频比特率:{}", audioBitRate);
        int sampleRate = grabber.getSampleRate();
        log.info("音频采样率:{}", sampleRate);

        if (audioBitRate < 1) {
            // 默认音频比特率
            audioBitRate = 128 * 1000;
        }

        // 录制/推流器 选择输出 out 输出路径
        FFmpegFrameRecorder record = new FFmpegFrameRecorder(out, width, height);
        record.setVideoOption("crf", "18");
        record.setGopSize(2);
        record.setFrameRate(frameRate);
        record.setVideoBitrate(bitRate);

        record.setAudioCodec(audioCodecId);
//        record.setAudioChannels(audioChannels);
        record.setAudioBitrate(audioBitRate);
        record.setSampleRate(sampleRate);
        record.setVideoCodec(videoCodecId);
        AVFormatContext fc = null;
        String rtmp = "rtmp";
        String flv = "flv";
        if (out.contains(rtmp) || out.indexOf(flv) > 0) {
            // 兼容rtmp处理: 封装格式flv 音频编码aac 视频编码h264
            record.setFormat(flv);
            record.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
            record.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            fc = grabber.getFormatContext();
        }
        record.start(fc);

        // ip 转封装
        //采集或推流导致的错误次数
        long errIndex = 0;
        //连续五次没有采集到帧则认为视频采集结束，程序错误次数超过1次即中断程序
        log.info("推流 {} 开始 => {}", ip, LocalDateTime.now());
        this.dataCache.set(DataCachePrefixConstant.HIK_PUSH_STATUS_IP + ip, 1);
        for (int noFrameIndex = 0; noFrameIndex < 5 || errIndex > 1; ) {
            try {
                //没有解码的音视频帧
                AVPacket pkt = grabber.grabPacket();
                if (pkt == null || pkt.size() <= 0 || pkt.data() == null) {
                    //空包记录次数跳过
                    int nfi = noFrameIndex++;
                    log.error("空包记录次数跳过" + nfi);
                    continue;
                }
                //不需要编码直接把音视频帧推出去
                errIndex += (record.recordPacket(pkt) ? 0 : 1);
                av_packet_unref(pkt);
                Integer pushStatus = this.dataCache.getInteger(DataCachePrefixConstant.HIK_PUSH_STATUS_IP + ip);
                if (null != pushStatus && pushStatus == 0) {
                    log.info("收到推流结束命令，退出推流：{}", ip);
                    break;
                }
            } catch (IOException e) {
                // 推流失败 如果失败err_index自增1
                log.error("推流失败:{}", e.getMessage());
                errIndex++;
            }
        }
        log.info("推流 {} 结束 => {}", ip, LocalDateTime.now());
    }
}
