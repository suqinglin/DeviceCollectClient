package com.nexless.ccommble.conn

/**
 * 蓝牙连接回调
 */
interface BluetoothListener {
    /**
     * 连接成功
     * @param status Int 状态值
     */
    fun onConnStatusSucc(status: Int)

    /**
     * 连接失败
     * @param status Int 状态值
     */
    fun onConnStatusFail(status: Int)

    /**
     * 接收数据
     * @param data ByteArray? 数据
     */
    fun onDataChange(data: ByteArray?)
}