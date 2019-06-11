package com.nexless.ccommble.scan;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Message;

import com.nexless.ccommble.util.CommHandler;
import com.nexless.ccommble.util.CommLog;

/**
 * Created by wxm on 2016/3/26.
 */
public class NexlessBluetoothScanner implements CommHandler.MessageHandler {
    private final static String TAG = "NexlessBluetoothScanner";
    private static final int SCANNER_STOP_WHAT = 1;
    private static final int BLE_SCANNER_DURATION = 2;//s
    private static NexlessBluetoothScanner mTerminusScanner;
    private final CommHandler mCommonHandler;
    private Context mContext;
    private BluetoothAdapter mBluetoothAdapter;
    private NexlessBLeScanner mTerminusScan;
    private NexlessScannerCallBack mBluetoothScannerCallBack;
    private boolean isBluetoothDiscovering = false;//蓝牙是否在搜索
    /**
     * 4.0蓝牙搜索回调
     */
    private NexlessBLeScanner.TerminusLeScanCallback mBleScanCallback = new NexlessBLeScanner.TerminusLeScanCallback() {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (mBluetoothScannerCallBack != null) {
                mBluetoothScannerCallBack.onScannerResultCallBack(device, rssi);
            }
        }
    };
    // 3.0蓝牙搜索结果，以广播的形式发送回来
    private BroadcastReceiver mDeviceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int rssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);
                if (mBluetoothScannerCallBack != null) {
                    mBluetoothScannerCallBack.onScannerResultCallBack(device, rssi);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (mBluetoothScannerCallBack != null) {
                    mBluetoothScannerCallBack.onScanFinished();
                }
                isBluetoothDiscovering = false;
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                // 蓝牙状态改变
                int currentState = intent.getIntExtra(
                        BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (currentState) {
                    case BluetoothAdapter.STATE_ON:
                        if (mBluetoothScannerCallBack != null && !isScanning()) {
                            startScan(mBluetoothScannerCallBack);
                        }
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        if (mBluetoothScannerCallBack != null && !isScanning()) {
                            startScan(mBluetoothScannerCallBack);
                        }
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        break;
                }
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                if (mBluetoothScannerCallBack != null && !isScanning()) {
                    startScan(mBluetoothScannerCallBack);
                }
            } else {
            }
        }
    };

    private NexlessBluetoothScanner(Context context) {
        this.mContext = context.getApplicationContext();
        mCommonHandler = new CommHandler(this);
        init();
    }

    public static NexlessBluetoothScanner getIntance(Context context) {
        if (mTerminusScanner == null) {
            mTerminusScanner = new NexlessBluetoothScanner(context);
        }
        return mTerminusScanner;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void init() {
        if (isBleBluetooth()) {
            final BluetoothManager bm = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bm.getAdapter();
            mTerminusScan = NexlessBLeScanner.getInstance(mContext);
        } else {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            IntentFilter intentFilter = new IntentFilter();
            // 发现蓝牙设备
            intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
            // 搜索完毕
            intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            // 蓝牙状态改变
            intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            // 蓝牙判断连接
            intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
            mContext.registerReceiver(mDeviceReceiver, intentFilter);
        }
    }

    public void startScan(NexlessScannerCallBack callBack) {
        startScan(callBack, BLE_SCANNER_DURATION * 60 * 1000);
    }

    public void startScan(NexlessScannerCallBack callBack, final long timeOutDuration) {
        if (mBluetoothAdapter == null) {
            if (callBack != null) {
                callBack.onScanFailed(ScanFailType.BLUETOOTH_DISENABLE);
            }
            return;
        }
        boolean isEnableSuccess = false;
        if (!mBluetoothAdapter.isEnabled()) {
            isEnableSuccess = mBluetoothAdapter.enable();
        }
        if ((!mBluetoothAdapter.isEnabled()) && (!isEnableSuccess)) {
            if (callBack != null) {
                callBack.onScanFailed(ScanFailType.BLUETOOTH_DISENABLE);
            }
            return;
        }

        this.mBluetoothScannerCallBack = callBack;

        isBluetoothDiscovering = true;
        if (isBleBluetooth()) {
            mCommonHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mTerminusScan.startLeScan(mBleScanCallback);
                    mCommonHandler.removeMessages(SCANNER_STOP_WHAT);
                    mCommonHandler.sendEmptyMessageDelayed(SCANNER_STOP_WHAT, timeOutDuration);
                }
            },300);
        } else {
            mBluetoothAdapter.startDiscovery();
        }

        if (mBluetoothScannerCallBack != null) {
            mBluetoothScannerCallBack.onScanStarted();
        }
    }

    public void stopScan() {
        if (!isBluetoothDiscovering) {
            CommLog.logE("stopScan fail, isBluetoothDiscovering = false");
            return;
        }
        isBluetoothDiscovering = false;

        if (isBleBluetooth()) {
            CommLog.logE("stopScan isBleBluetooth = true");
            mTerminusScan.stopLeScan();
        } else {
            CommLog.logE("stopScan isBleBluetooth = false");
            mBluetoothAdapter.cancelDiscovery();
        }

        if (mBluetoothScannerCallBack != null) {
            mBluetoothScannerCallBack.onScanFinished();
        }
        mCommonHandler.removeCallbacksAndMessages(null);
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public boolean isBluetoothEnable() {
        if (mBluetoothAdapter == null) {
            return false;
        }
        return mBluetoothAdapter.isEnabled();
    }

    public boolean isScanning() {
        return isBluetoothDiscovering;
    }

    public void destory() {
        stopScan();
        mBluetoothScannerCallBack = null;
        mBluetoothAdapter = null;
        mContext = null;
        mTerminusScanner = null;
        if (mTerminusScan != null) {
            mTerminusScan.destroy();
        }
        mCommonHandler.removeMessages(SCANNER_STOP_WHAT);
    }

    /**
     * 是否支持4.0蓝牙
     *
     * @return
     */
    public boolean isBleBluetooth() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 &&
                mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    private boolean isUpLOLLIPOP() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    @Override
    public void handleMessage(Message msg) {
        stopScan();
    }

    public enum ScanFailType {
        BLUETOOTH_DISENABLE
    }

    public interface NexlessScannerCallBack {
        void onScannerResultCallBack(BluetoothDevice device, int rssi);

        void onScanFinished();

        void onScanStarted();

        void onScanFailed(ScanFailType type);
    }
}
