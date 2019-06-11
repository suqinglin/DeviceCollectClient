package com.nexless.ccommble.util;

import android.util.Log;

/**
 * @date: 2019/1/22
 * @author: su qinglin
 * @description: Log管理类
 */
public class CommLog
{
    public static void logE(String tag,String info)
    {
        if(CommConstant.DEBUG)
        {
            Log.e(tag,info);
        }
    }
    public static void logE(String info)
    {
        logE("NexlessLog",info);
    }
}
