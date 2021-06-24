package com.oldwei.hikdev.sdk.structure;

import com.oldwei.hikdev.constant.HikConstant;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 13:26
 */
public class NET_DVR_SCENE_INFO extends Structure {
    public int dwSceneID;              //场景ID, 0 - 表示该场景无效
    public byte[] bySceneName = new byte[HikConstant.NAME_LEN];  //场景名称
    public byte byDirection;            //监测方向 1-上行，2-下行，3-双向，4-由东向西，5-由南向北，6-由西向东，7-由北向南，8-其它
    public byte[] byRes1 = new byte[3];              //保留
    public NET_DVR_PTZPOS struPtzPos;             //Ptz 坐标
    public byte[] byRes2 = new byte[64] ;            //保留
}
