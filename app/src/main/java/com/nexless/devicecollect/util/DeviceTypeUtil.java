package com.nexless.devicecollect.util;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.text.TextUtils;

import com.nexless.devicecollect.R;

/**
 * @date: 2019/7/23
 * @author: su qinglin
 * @description:
 */
public class DeviceTypeUtil {

    private static boolean isKeyType(String remark, int stringArrayId, Context context) {
        if (TextUtils.isEmpty(remark)) {
            return false;
        }
        String[] devices = context.getResources()
                .getStringArray(stringArrayId);
        for (int i = 0; i < devices.length; i++) {
            String name = remark.toLowerCase();
            if (!TextUtils.isEmpty(name) && name.equals(devices[i].toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isGateway(String remark, Context context) {
        return isKeyType(remark, R.array.gateway, context);
    }

    public enum KeyType {
        GATEWAY, OTHER
    }
}
