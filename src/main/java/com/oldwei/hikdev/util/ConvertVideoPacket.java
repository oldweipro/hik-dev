package com.oldwei.hikdev.util;

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

import java.io.IOException;
import java.io.PipedInputStream;
import java.time.LocalDateTime;

import static org.bytedeco.ffmpeg.global.avcodec.av_packet_unref;

/**
 * rtsp转rtmp（转封装方式）
 *
 * @author oldwei
 * @date 2021-5-19 18:42
 */
@Slf4j
@RequiredArgsConstructor
public class ConvertVideoPacket {

    private final DataCache dataCache;
    FFmpegFrameGrabber grabber = null;
    FFmpegFrameRecorder record = null;
    int width = -1, height = -1;

    /**
     * 视频编码ID
     */
    protected int codecId;
    /**
     * 帧率
     */
    protected double frameRate;
    /**
     * 比特率
     */
    protected int bitRate;

    /**
     * 音频参数
     * 想要录制音频，这三个参数必须有：audioChannels > 0 && audioBitrate > 0 && sampleRate > 0
     */
    private int audioChannels;
    private int audioBitRate;
    private int sampleRate;
    /**
     * 音频编码ID
     */
    private int audioCodecId;

    /**
     * 选择视频源
     *
     * @param pis sdk获取的裸流
     * @throws IOException
     */
    public ConvertVideoPacket fromPis(PipedInputStream pis) {
        grabber = new FFmpegFrameGrabber(pis, 0);
        return this;
    }

    /**
     * 选择视频源
     *
     * @param rtspUrl rtsp流
     * @return
     */
    public ConvertVideoPacket fromRtsp(String rtspUrl) {
        grabber = new FFmpegFrameGrabber(rtspUrl);
        if (rtspUrl.contains(VideoConstant.RTSP)) {
            grabber.setOption("rtsp_transport", "tcp");
        }
        return this;
    }

    /**
     * 设置流抓取器
     *
     * @return
     * @throws FrameGrabber.Exception
     */
    public ConvertVideoPacket setGrabber() throws FrameGrabber.Exception {

        // 开始之后ffmpeg会采集视频信息，之后就可以获取音视频信息
        grabber.start();
        //一个opencv视频帧转换器
        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
        int i = 0;
        Frame frame = null;
        while (i < 10) {
            frame = grabber.grabFrame();
            if (frame.image != null) {
                log.info("取到第一帧：" + i);
                break;
            }
            i++;
        }
        IplImage iplImage = converter.convert(frame);
        //保存第一帧图片
        opencv_imgcodecs.cvSaveImage("./pic/first_frame.jpg", iplImage);
        if (width < 0 || height < 0) {
            width = grabber.getImageWidth();
            height = grabber.getImageHeight();
        }
        // 视频参数
        audioCodecId = grabber.getAudioCodec();
        log.info("音频编码：" + audioCodecId);
        codecId = grabber.getVideoCodec();
        // 帧率
        frameRate = grabber.getVideoFrameRate();
        // 比特率
        int videoCodec = grabber.getVideoCodec();
        log.info("视频编码：" + videoCodec);
        bitRate = grabber.getVideoBitrate();
        // 音频参数
        // 想要录制音频，这三个参数必须有：audioChannels > 0 && audioBitrate > 0 && sampleRate > 0
        audioChannels = grabber.getAudioChannels();
        audioBitRate = grabber.getAudioBitrate();

        if (audioBitRate < 1) {
            // 默认音频比特率
            audioBitRate = 128 * 1000;
        }
        return this;
    }

    /**
     * 选择输出
     *
     * @param out
     * @throws IOException
     * @author eguid
     */
    public ConvertVideoPacket to(String out) throws IOException {
        // 录制/推流器
        record = new FFmpegFrameRecorder(out, width, height);
        record.setVideoOption("crf", "18");
        record.setGopSize(2);
        record.setFrameRate(frameRate);
        record.setVideoBitrate(bitRate);

        record.setAudioChannels(audioChannels);
        record.setAudioBitrate(audioBitRate);
        record.setSampleRate(sampleRate);
        record.setVideoCodec(avcodec.AV_CODEC_ID_MPEG4);
        AVFormatContext fc = null;
        if (out.contains("rtmp") || out.indexOf("flv") > 0) {
            // 封装格式flv
            record.setFormat("flv");
            record.setAudioCodecName("aac");
            record.setVideoCodec(codecId);
            fc = grabber.getFormatContext();
        }
        record.start(fc);
        return this;
    }

    /**
     * 转封装
     *
     * @throws IOException
     * @author eguid
     */
    public void go(String ip) throws IOException {
        //采集或推流导致的错误次数
        long errIndex = 0;
        //连续五次没有采集到帧则认为视频采集结束，程序错误次数超过1次即中断程序
        log.info("推流开始 => {}",ip, LocalDateTime.now());
        for (int noFrameIndex = 0; noFrameIndex < 5 || errIndex > 1; ) {
            AVPacket pkt = null;
            try {
                //没有解码的音视频帧
                pkt = grabber.grabPacket();
                if (pkt == null || pkt.size() <= 0 || pkt.data() == null) {
                    //空包记录次数跳过
                    int i = noFrameIndex++;
                    System.err.println("空包记录次数跳过" + i);
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
                errIndex++;
            }
        }
        log.info("推流 {} 结束 => {}",ip, LocalDateTime.now());
    }
}
