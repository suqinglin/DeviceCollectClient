package com.nexless.devicecollect.util;

/**
 * @date: 2019/9/17
 * @author: su qinglin
 * @description:
 */
public interface Function {

    int SUPPORT_TORKEN = 1;
    int SUPPORT_UUID = 1 << 1;
    int SUPPORT_MAC = 1 << 2;
    int SUPPORT_MODEL = 1 << 3;
    int SUPPORT_SN = 1 << 4;
    int SUPPORT_TIME = 1 << 5;
    int SUPPORT_HW_VER = 1 << 6;
    int SUPPORT_FW_VER = 1 << 7;
    int SUPPORT_MANUF = 1 << 8;
    int SUPPORT_TOOL = 1 << 9;
    int SUPPORT_INFO = 1 << 10;
    int SUPPORT_RESERVED = 1 << 11;
    int SUPPORT_FUN = 1 << 15;
}
