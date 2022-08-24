package com.oldwei.hikdev.entity.config;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author oldwei
 * @date 2021-7-7 14:42
 */
@Data
public class DeviceLoginDTO implements Serializable {
    private static final long serialVersionUID = 7342965103728984523L;
    @NotBlank(message = "[iPv4address]IP地址不能为空")
    private String ipv4Address;
    @NotBlank(message = "[username]用户名不能为空")
    private String username;
    @NotBlank(message = "[password]密码不能为空")
    private String password;
    @NotBlank(message = "[commandPort]端口不能为空")
    private String commandPort;
    private String title;
    private String screenPicture;
    private Boolean useAsync = true;
    private Integer charEncodeType;
    private List<Integer> analogChannelIds = new ArrayList<>();
    private List<Integer> digitalChannelIds = new ArrayList<>();
    private List<Integer> allChannelIds = new ArrayList<>();
    /**
     * 登陆状态，海康设备返回的登陆id
     */
    private Integer loginId;

}
