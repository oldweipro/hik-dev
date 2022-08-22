package com.oldwei.hikdev.structure;

import com.sun.jna.Structure;

/**
 * 校时结构参数
 *
 * @author oldwei
 * @date 2021-5-18 13:22
 */
public class NET_DVR_TIME extends Structure {
    /**
     * 年
     */
    public int dwYear;
    /**
     * 月
     */
    public int dwMonth;
    /**
     * 日
     */
    public int dwDay;
    /**
     * 时
     */
    public int dwHour;
    /**
     * 分
     */
    public int dwMinute;
    /**
     * 秒
     */
    public int dwSecond;

    @Override
    public String toString() {
        return "NET_DVR_TIME.dwYear: " + dwYear + "\n" + "NET_DVR_TIME.dwMonth: \n" + dwMonth + "\n" + "NET_DVR_TIME.dwDay: \n" + dwDay + "\n" + "NET_DVR_TIME.dwHour: \n" + dwHour + "\n" + "NET_DVR_TIME.dwMinute: \n" + dwMinute + "\n" + "NET_DVR_TIME.dwSecond: \n" + dwSecond;
    }

    /**
     * 格式化时间 yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public String toStringTimeDateFormat() {
        return String.format("%02d-%02d-%02d %02d:%02d:%02d", dwYear, dwMonth, dwDay, dwHour, dwMinute, dwSecond);
    }

    /**
     * 用于列表中显示
     *
     * @return
     */
    public String toStringTime() {
        return String.format("%02d/%02d/%02d%02d:%02d:%02d", dwYear, dwMonth, dwDay, dwHour, dwMinute, dwSecond);
    }

    /**
     * 存储文件名使用
     *
     * @return
     */
    public String toStringTitle() {
        return String.format("Time%02d%02d%02d%02d%02d%02d", dwYear, dwMonth, dwDay, dwHour, dwMinute, dwSecond);
    }
}
