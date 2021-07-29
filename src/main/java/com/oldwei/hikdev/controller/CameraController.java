package com.oldwei.hikdev.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author oldwei
 * @date 2021-7-10 15:25
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/ipc")
public class CameraController {

    @GetMapping("")
    public JSONObject get() {
        return null;
    }
}
