package com.oldwei.hikdev.structure;

import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 13:42
 */
public class NET_DVR_SYSTEM_TIME extends Structure {
    /**
     * 年
     */
    public short wYear;

    /**
     * 月
     */
    public short wMonth;

    /**
     * 日
     */
    public short wDay;

    /**
     * 时
     */
    public short wHour;

    /**
     * 分
     */
    public short wMinute;

    /**
     * 秒
     */
    public short wSecond;

    /**
     * 毫秒
     */
    public short wMilliSec;
    public byte[] byRes = new byte[2];

    /**
     * 格式化时间 yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public String toStringTimeDateFormat() {
        return String.format("%02d-%02d-%02d %02d:%02d:%02d", wYear, wMonth, wDay, wHour, wMinute, wSecond);
    }

    /**
     * 用于列表中显示
     *
     * @return
     */
    public String toStringTime() {
        return String.format("%02d/%02d/%02d%02d:%02d:%02d", wYear, wMonth, wDay, wHour, wMinute, wSecond);
    }

    /**
     * 存储文件名使用
     *
     * @return
     */
    public String toStringTitle() {
        return String.format("Time%02d%02d%02d%02d%02d%02d", wYear, wMonth, wDay, wHour, wMinute, wSecond);
    }
}
