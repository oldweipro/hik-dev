package com.oldwei.hikdev.util;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;

import java.nio.charset.Charset;

public class RtspServerHttpUtil {

    private static final String auth = HttpUtil.buildBasicAuth("demo", "demo", Charset.defaultCharset());
    private static final String host = "http://127.0.0.1:41319/";

    public static String get(String url) {
        return get(url, "");
    }

    public static String get(String url, String body) {
        return HttpRequest.get(host + url).body(body).header(Header.AUTHORIZATION, auth).execute().body();
    }

    public static String post(String url) {
        return post(url, "");
    }

    public static String post(String url, String body) {
        return HttpRequest.post(host + url).body(body).header(Header.AUTHORIZATION, auth).execute().body();
    }

}
