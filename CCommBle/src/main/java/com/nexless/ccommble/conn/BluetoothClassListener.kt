package com.nexless.ccommble.conn

/**
 * 蓝牙回调
 */
abstract class BluetoothClassListener : BluetoothListener {
    /**
     * 连接成功
     * @param status Int
     */
    override fun onConnStatusSucc(status: Int) {

    }

    /**
     * 连接失败
     * @param status Int
     */
    override fun onConnStatusFail(status: Int) {

    }

    /**
     * 接收数据
     * @param data ByteArray?
     */
    override fun onDataChange(data: ByteArray?) {

    }
}