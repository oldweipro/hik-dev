package com.oldwei.hikdev.sdk.structure;

import com.sun.jna.Union;

/**
 * @author oldwei
 * @date 2021-5-18 12:16
 */
public class uStruAlarm extends Union {
    public byte[] byUnionLen = new byte[128];
    public struIOAlarm struioAlarm = new struIOAlarm();
    public struAlarmHardDisk strualarmHardDisk = new struAlarmHardDisk();
    public struAlarmChannel strualarmChannel = new struAlarmChannel();
    public struRecordingHost strurecordingHost = new struRecordingHost();
}
