package com.oldwei.hikdev.structure;

import com.sun.jna.Structure;

/**
 * 图片质量
 *
 * @author oldwei
 * @date 2022/8/18 9:16
 */
public class NET_DVR_JPEGPARA extends Structure {
    /*注意：当图像压缩分辨率为VGA时，支持0=CIF, 1=QCIF, 2=D1抓图，
    当分辨率为3=UXGA(1600x1200), 4=SVGA(800x600), 5=HD720p(1280x720),6=VGA,7=XVGA, 8=HD900p
    仅支持当前分辨率的抓图*/
    public short wPicSize = 0;                /* 0=CIF, 1=QCIF, 2=D1 3=UXGA(1600x1200), 4=SVGA(800x600), 5=HD720p(1280x720),6=VGA*/
    public short wPicQuality = 2;            /* 图片质量系数 0-最好 1-较好 2-一般 */
}
