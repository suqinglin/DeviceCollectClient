package com.nexless.devicecollect.util;

/**
 * .net MD5加密和java不一样的转换桥接工具类
 * Created by wangkun23 on 2018/12/26.
 */
public class DoNetUnicodeUtils {

    private DoNetUnicodeUtils() {

    }

    /**
     * @param source
     * @return
     */
    public static byte[] toUnicodeMC(String source) {
        byte[] bytes = new byte[source.length() * 2];
        for (int i = 0; i < source.length(); i++) {
            int code = source.charAt(i) & 0xffff;
            bytes[i * 2] = (byte) (code & 0x00ff);
            bytes[i * 2 + 1] = (byte) (code >> 8);
        }
        return bytes;
    }
}
