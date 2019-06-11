package com.nexless.ccommble.util;

import com.nexless.ccommble.conn.ConnectionConstants;

/**
 * @date: 2019/5/6
 * @author: su qinglin
 * @description:
 */
public class BleStatusUtil {

    public static final String RST_SUCC = "0x0";
    public static final String RST_NOT_ORIGINAL_STATE = "0x20";
    public static final String RST_NO_USER_KEY = "0x21";
    public static final String RST_NO_LOGS = "0x3";
    public static final String RST_OTHERS = "0xFE";

    public static String getConnectStatusMsg(int status) {

        if (status == ConnectionConstants.STATUS_DATA_WRITE_FAIL) {
            return "写入数据失败";
        } else if (status == ConnectionConstants.STATUS_DATA_READ_TIMEOUT) {
            return "读取数据超时";
        } else {
            return "连接设备失败";
        }
    }

    public static String getResultMsg(String result) {

        if (RST_NOT_ORIGINAL_STATE.equals(result)) {
            return "非初始状态";
        } else if (RST_NO_USER_KEY.equals(result)) {
            return "未下载用户密钥";
        } else if (RST_OTHERS.equals(result)) {
            return "其他错误";
        } else if (RST_NO_LOGS.equals(result)) {
            return "暂无更多日志";
        } else {
            return "未知错误";
        }
    }
}
