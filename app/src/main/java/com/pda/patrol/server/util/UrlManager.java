package com.pda.patrol.server.util;

public class UrlManager {
    private static final String BASE_URL = "http://sw.topiot.co";
    public static String formatUrl(String url) {
        if(url == null || "".equals(url)) {
            return "";
        }
        return url.startsWith("http") ? url : BASE_URL + url;
    }

    /**
     * 登录
     */
    public static final String USER_LOGIN = "/api/v1/login";

    /**
     * 获取单条记录详情
     */
    public static final String GET_RFID_INFO = "/api/v1/tbrfid";
}
