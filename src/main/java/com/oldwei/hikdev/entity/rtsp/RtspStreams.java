package com.oldwei.hikdev.entity.rtsp;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class RtspStreams implements Serializable {
    private static final long serialVersionUID = 2601333357344782941L;

    private String uuid;
    private String name;
    private Map<String, Object> channels;
}
