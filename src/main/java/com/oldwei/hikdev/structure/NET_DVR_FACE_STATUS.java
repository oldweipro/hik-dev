package com.oldwei.hikdev.structure;

import com.oldwei.hikdev.constant.HikConstant;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-17 16:55
 */
public class NET_DVR_FACE_STATUS extends Structure {
    public int dwSize;
    public byte[] byCardNo = new byte[HikConstant.ACS_CARD_NO_LEN];
    public byte[] byErrorMsg = new byte[HikConstant.ERROR_MSG_LEN];
    public int dwReaderNo;
    // 人脸读卡器状态，按字节表示，0-失败，1-成功，2-重试或人脸质量差，
    // 3-内存已满(人脸数据满)，4-已存在该人脸，5-非法人脸 ID，6-算法建模失败，7-未下发卡权限，
    // 8-未定义（保留），9-人眼间距小距小，10-图片数据长度小于 1KB，11-图片格式不符（png/jpg/bmp），
    // 12-图片像素数量超过上限，13-图片像素数量低于下限，14-图片信息校验失败，15-图片解码失败，
    // 16-人脸检测失败，17-人脸评分失败
    public byte byRecvStatus;
    public byte[] byRes = new byte[131];
}
