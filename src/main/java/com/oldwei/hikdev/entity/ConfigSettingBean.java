package com.oldwei.hikdev.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

/**
 * @author oldwei
 * @date 2021-9-23 16:57
 */
@Data
public class ConfigSettingBean implements Serializable {
    private static final long serialVersionUID = -5745823906419169257L;
    private String uploadDataUrl;
    private Integer udpPort;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long projectId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long categoryId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long tenantId;
}
