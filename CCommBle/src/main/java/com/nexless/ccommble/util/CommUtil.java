package com.nexless.ccommble.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.nexless.ccommble.codec.DecoderException;
import com.nexless.ccommble.codec.binary.Hex;
import com.nexless.ccommble.data.BagLockAESUtils;

import java.util.Locale;

import static android.provider.Settings.System.getString;

/**
 * @date: 2019/1/22
 * @author: su qinglin
 * @description: 公共工具类
 */

public class CommUtil
{
    private static int ORDER_NUM = -1;//aiot操作需要的 到255在置0
    /**
     * 将字符串按mac的格式返回
     * @param mac
     * @return
     */
    public static String splitMac(String mac)
    {
        if(TextUtils.isEmpty(mac))
        {
            return "";
        }
        if(mac.length() %2 != 0 || mac.length() != 12)
        {
            return "";
        }
        return mac.substring(0,2)+":"+mac.substring(2,4)+":"+mac.substring(4,6)+":"+mac.substring(6,8)+":"+mac.substring(8,10)+":"+mac.substring(10,12);
    }

    /**
     * 将16进制字符串转换为byte[]
     *
     * @param str
     * @return
     */
    public static byte[] hexStringToBytes(String str) {
        if(str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for(int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }

        return bytes;
    }

    public static int getOrderNum()
    {
        ORDER_NUM = ORDER_NUM+1;
        if(ORDER_NUM > 255)
        {
            ORDER_NUM = 0;
        }
        return ORDER_NUM;
    }

    /**
     * 获取设备唯一ID
     * @param context
     * @return
     */
    public static String getDeviceUniqueID(Context context) {
        String id = getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (TextUtils.isEmpty(id)) {
            id = Build.SERIAL;
        }
//        return "1507bfd3f791a39daf4";
        return id;
    }

    /**
     * 获取隐藏后的手机号（隐藏中间四位）
     * @param mobile 原始手机号
     * @return
     */
    public static String getHiddenMobile(String mobile) {
        return mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
    }

    public static byte[] long2Bytes(long num) {
        byte[] byteNum = new byte[4];
        for (int ix = 4; ix < 8; ++ix) {
            int offset = 64 - (ix + 1) * 8;
            byteNum[ix - 4] = (byte) ((num >> offset) & 0xff);
        }
        return byteNum;
    }

    /**
     * 设置APP语言
     * @param context
     * @param myLocale
     */
    public static void changeAppLanguage(Context context, Locale myLocale) {
//        String sta = Store.getLanuageIsChinese() ? "zh" : "en";//这是SharedPreferences工具类，用于保存设置，代码很简单，自己实现吧
        // 本地语言设置
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    /**
     * 获取PackageManager对象
     *
     * @param context
     * @return
     */
    private static PackageManager getPackageManager(Context context) {
        return context.getPackageManager();
    }

    /**
     * 获取包名
     *
     * @param context
     * @return
     */
    public static String getPackageName(Context context) {
        return context.getPackageName();
    }

    /**
     * 获取VersionName(版本名称)
     *
     * @param context
     * @return 失败时返回""
     */
    public static String getVersionName(Context context) {
        PackageManager packageManager = getPackageManager(context);
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(context), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean checkCrc(String data, String crc) throws DecoderException {
        int dataCrc = BagLockAESUtils.crc16(0, Hex.decodeHex(data));
        int crcInt = Integer.parseInt(crc, 16);
        return dataCrc == crcInt;
    }
}
