package com.nexless.ccommble.conn

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Build
import com.nexless.ccommble.util.CommLog
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * 蓝牙连接
 */
class BluetoothConnection(context: Context,devName: String, mac: String, endIdentify:Array<String>?,listener: BluetoothListener) {
    private val TAG = "BluetoothConnectionCallback"
    private var mac: String
    private var devName: String
    private var endIdentify:Array<String>?
    private var autoDisconnTime: Int
    private var reConnect: Boolean
    private val connStartTime: Long
    private val bluetoothListener: BluetoothListener
    private val context: Context
    private var timerDelyDisconn: Disposable? = null
    private var timerDelyConn: Disposable? = null
    private var bluetoothConnectionCallback: BluetoothConnectionCallback? = null
    private var sendData: ByteArray? = null

    init {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bleAdapter = manager.adapter
        autoDisconnTime = -1
        reConnect = false
        connStartTime = System.currentTimeMillis()
        bluetoothListener = listener
        this.context = context
        this.mac = mac
        this.devName = devName
        this.endIdentify = endIdentify
        val device = bleAdapter.getRemoteDevice(mac)
        createBluetoothCallback(devName,endIdentify,device)
    }

    fun disConnDevice() {
        closeTimerDelyDisconn()
        closeTimerDelyConn()
        if (bluetoothConnectionCallback != null) {
            CommLog.logE(TAG, "disConnDevice bluetoothConnectionCallback != null")
            bluetoothConnectionCallback!!.closeTimerReadTimeout()
            bluetoothConnectionCallback!!.closeTimerWriteTimeout()
            bluetoothConnectionCallback!!.disConnGatt()
            bluetoothConnectionCallback!!.closeGatt()
            bluetoothConnectionCallback = null
        }
    }

    fun sendData(time: Int, data: ByteArray, reConnect: Boolean, endIdentify: Array<String>?) {
        CommLog.logE(TAG, "sendData data = " + String(data))
        closeTimerDelyDisconn()
        this.autoDisconnTime = time
        this.reConnect = reConnect
        this.sendData = data
        bluetoothConnectionCallback?.writeData(DataPackages(data),endIdentify)
    }

    fun setCanReceiveData() {
        bluetoothConnectionCallback?.setCanReceiveData()
    }

    private fun closeTimerDelyDisconn() {
        if (timerDelyDisconn != null && !timerDelyDisconn!!.isDisposed) {
            timerDelyDisconn!!.dispose()
            timerDelyDisconn = null
        }
    }

    private fun closeTimerDelyConn() {
        if (timerDelyConn != null && !timerDelyConn!!.isDisposed) {
            timerDelyConn!!.dispose()
            timerDelyConn = null
        }
    }

    private fun createBluetoothCallback(devName: String,endIdentify:Array<String>?,device: BluetoothDevice) {
        closeTimerDelyDisconn()
        closeTimerDelyConn()
        bluetoothConnectionCallback = BluetoothConnectionCallback(devName,endIdentify,object : BluetoothClassListener() {
            override fun onConnStatusSucc(status: Int) {
                super.onConnStatusSucc(status)
                CommLog.logE(TAG, "onConnStatusSucc status = $status")
                if (status == ConnectionConstants.STATUS_CONN_ENNOTIFY_SUCC || status == ConnectionConstants.STATUS_CONN_DISCONN) {
                    bluetoothListener.onConnStatusSucc(status)
                }
            }

            override fun onConnStatusFail(status: Int) {
                super.onConnStatusFail(status)
                CommLog.logE(TAG, "onConnStatusFail status = $status")
                closeTimerDelyDisconn()
                closeTimerDelyConn()
//                bluetoothConnectionCallback?.disConnGatt()
                val timeRange = System.currentTimeMillis() - connStartTime
                CommLog.logE(TAG, "onConnStatusFail timeRange = $timeRange connStartTime = $connStartTime")
                if (timeRange < TIME_CONN_TIMEOUT && reConnect) {
                    timerDelyConn = Observable.timer(1000, TimeUnit.MILLISECONDS)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                CommLog.logE(TAG, "onConnStatusFail timerDelyConn")
                                createBluetoothCallback(devName,endIdentify,device)
                                if (sendData != null) {
                                    bluetoothConnectionCallback?.writeData(DataPackages(sendData!!), endIdentify)
                                }
                            }
                } else {
                    CommLog.logE(TAG, "onConnStatusFail completeFail")
                    closeTimerDelyConn()
                    bluetoothListener.onConnStatusFail(status)
                }
            }

            override fun onDataChange(data: ByteArray?) {
                super.onDataChange(data)
                closeTimerDelyConn()
                closeTimerDelyDisconn()
                CommLog.logE(TAG, "onDataChange11")
                bluetoothListener.onDataChange(data)
                if (autoDisconnTime == 0) {
                    CommLog.logE(TAG, "autoDisconnTime == 0")
                    bluetoothConnectionCallback?.disConnGatt()
                } else if (autoDisconnTime > 0) {
                    timerDelyDisconn = Observable.timer(autoDisconnTime.toLong(), TimeUnit.MILLISECONDS)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                CommLog.logE(TAG, "autoDisconnTime.toLong() == 0")
                                bluetoothConnectionCallback?.disConnGatt()
                            }
                }
            }
        })
        connDevice(device)
    }

    private fun connDevice(device: BluetoothDevice) {
        CommLog.logE(TAG, "BluetoothConnectionCallback->start connect")
        if (Build.VERSION.SDK_INT >= 23) {
            device.connectGatt(context, false, bluetoothConnectionCallback, BluetoothDevice.TRANSPORT_LE)
        } else {
            try {
                val method = device.javaClass.getMethod("connectGatt", Context::class.java, Boolean::class.javaObjectType,
                        BluetoothGattCallback::class.java, Int::class.javaPrimitiveType)
                method.invoke(device, context, false, bluetoothConnectionCallback, BluetoothDevice.TRANSPORT_LE)
            } catch (e: Exception) {
                device.connectGatt(context, false, bluetoothConnectionCallback)
            }
        }
    }
}