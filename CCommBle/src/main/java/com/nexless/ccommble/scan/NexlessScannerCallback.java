package com.nexless.ccommble.scan;

import android.bluetooth.BluetoothDevice;

/**
 * @date: 2019/3/20
 * @author: su qinglin
 * @description:
 */
public interface NexlessScannerCallback {

    void onScannerResultCallBack(BluetoothDevice device, int rssi);

    void onScanFinished();

    void onScanStarted();

    void onScanFailed();

}
