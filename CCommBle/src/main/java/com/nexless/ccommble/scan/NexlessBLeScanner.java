package com.nexless.ccommble.scan;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import com.nexless.ccommble.util.CommLog;

/**
 * Created by Wufang on 16-3-18.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NexlessBLeScanner implements BluetoothAdapter.LeScanCallback {
    private static NexlessBLeScanner instance;
    private final String TAG = NexlessBLeScanner.class.getName();
    private Context mContext;
    private BluetoothAdapter mBluetoothAdapter;
    private TerminusLeScanCallback mCallback;

    private NexlessBLeScanner(Context context) {
        mContext = context;
        init();
    }

    public static NexlessBLeScanner getInstance(Context context) {
        if (instance == null)
            instance = new NexlessBLeScanner(context);
        return instance;
    }

    public void destroy() {
        mCallback = null;
        instance = null;
        mContext = null;
        mBluetoothAdapter = null;
    }

    public void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 &&
                mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            final BluetoothManager bm = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bm.getAdapter();
            if (mBluetoothAdapter == null) {
                CommLog.logE("--不是4.0的蓝牙");
            } else {
                CommLog.logE("--不是4.0的蓝牙");
            }
        }
    }

    public void startLeScan(TerminusLeScanCallback callback) {
        if (mBluetoothAdapter == null) {
            return;
        }
        CommLog.logE("LeScan startScan ");
        this.mCallback = callback;
        mBluetoothAdapter.startLeScan(this);
    }

    public void stopLeScan() {
        if (mBluetoothAdapter == null) {
            return;
        }
        CommLog.logE("LeScan stopScan ");
        mBluetoothAdapter.stopLeScan(this);
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (mCallback != null) {
            mCallback.onLeScan(device, rssi, scanRecord);
        }
    }

    public interface TerminusLeScanCallback {
        void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord);
    }
}
