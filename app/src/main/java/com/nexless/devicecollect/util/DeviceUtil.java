package com.nexless.devicecollect.util;

import android.text.TextUtils;

import com.nexless.ccommble.codec.DecoderException;
import com.nexless.ccommble.codec.binary.Hex;
import com.nexless.devicecollect.model.DeviceInfo;

/**
 * @date: 2019/6/10
 * @author: su qinglin
 * @description:
 */
public class DeviceUtil {
    public static DeviceInfo convertDeviceInfo(String infoStr) throws DecoderException {
        if (TextUtils.isEmpty(infoStr) || !infoStr.contains(",")) {
            return null;
        }
        String[] infoArr = infoStr.split(",");
        DeviceInfo deviceInfo = new DeviceInfo();
        for (int i = 0; i < infoArr.length; i++) {
            String info = infoArr[i];
            if (TextUtils.isEmpty(info) || !info.contains(":")) {
                continue;
            }
            String title = info.split(":")[0];
            String content = info.split(":")[1];
            switch (title) {
                case DeviceInfo.UUID:
                    if (!"00".equals(content)) {
                        deviceInfo.setUuid(content);
                    }
                    break;
                case DeviceInfo.MAC:
                    deviceInfo.setMac(content);
                    break;
                case DeviceInfo.MODEL:
                    byte[] bytes = Hex.decodeHex(content.replace("00", ""));
                    deviceInfo.setModel(new String(bytes));
                    break;
                case DeviceInfo.SN:
                    deviceInfo.setSn(content);
                    break;
                case DeviceInfo.TIME:
                    deviceInfo.setTime(Long.valueOf(content));
                    break;
                case DeviceInfo.HW_VER:
                    deviceInfo.setHwVer(convertVersion556(Integer.valueOf(content)));
                    break;
                case DeviceInfo.FW_VER:
                    deviceInfo.setFwVer(convertVersion556(Integer.valueOf(content)));
                    break;
                case DeviceInfo.MANUFACTURE:
                    deviceInfo.setManufId(Integer.valueOf(content));
                    break;
                case DeviceInfo.TOOL:
                    deviceInfo.setToolId(Integer.valueOf(content));
                    break;
            }
        }
        return deviceInfo;
    }

    public static String convertVersion556(int version) {
        int ver1 = (version >> 11) & 0xFF;
        int ver2 = (version & (0xFF >> 5)) >> 6;
        int ver3 = version & (0xFF >> 2);
        return ver1 + "." + ver2 + "." + ver3;
    }
}
