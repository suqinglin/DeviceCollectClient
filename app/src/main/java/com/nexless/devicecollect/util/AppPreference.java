package com.nexless.devicecollect.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.nexless.devicecollect.AppConstants;

/**
 * @date: 2019/1/22
 * @author: su qinglin
 * @description: Preference工具类
 */

public class AppPreference
{
    public static final String LOGIN_TOKEN = "login.token";
    public static final String USER_PHONE = "user.phone";
    public static final String USER_PASSWORD = "user.password";
    private static SharedPreferences mSharedPreferences;
    private static Context mContext;
    public static void initSharedPreferences(Context context)
    {
        mContext = context;
        if(mSharedPreferences == null)
        {
            mSharedPreferences = context.getSharedPreferences(AppConstants.APPPREFERENCE_FILE_NAME,Context.MODE_PRIVATE);
        }
    }
    public static String getStringWithId(int id)
    {
        return mContext.getString(id);
    }
    public static void putInt(String tag,int vaule)
    {
        mSharedPreferences.edit().putInt(tag,vaule).apply();
    }
    public static int getInt(String tag,int defVaule)
    {
        return mSharedPreferences.getInt(tag,defVaule);
    }
    public static void putString(String tag,String vaule)
    {
        mSharedPreferences.edit().putString(tag,vaule).apply();
    }
    public static String getString(String tag,String defVaule)
    {
        return mSharedPreferences.getString(tag,defVaule);
    }
    public static void putBoolean(String tag,boolean b)
    {
        mSharedPreferences.edit().putBoolean(tag,b).apply();
    }
    public static boolean getBoolean(String tag,boolean defVaule) {
        return mSharedPreferences.getBoolean(tag,defVaule);
    }
}
