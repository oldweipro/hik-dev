package com.oldwei.hikdev.structure;

import com.sun.jna.Structure;
import com.sun.jna.examples.win32.W32API;

/**
 * 软解码预览参数
 * @author oldwei
 * @date 2021-5-19 18:13
 */
public class NET_DVR_CLIENTINFO extends Structure {
    public int lChannel = 1;
    public int lLinkMode;
    public W32API.HWND hPlayWnd;
    public String sMultiCastIP;
}
