package com.nexless.devicecollect;

import android.app.Application;

import com.nexless.ccommble.conn.ConnectionHelper;
import com.nexless.devicecollect.util.AppPreference;

/**
 * @date: 2019/5/5
 * @author: su qinglin
 * @description:
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        LitePal.initialize(this);
        //初始化蓝牙连接
        ConnectionHelper.getInstance().init(this);
        AppPreference.initSharedPreferences(this);
    }
}
