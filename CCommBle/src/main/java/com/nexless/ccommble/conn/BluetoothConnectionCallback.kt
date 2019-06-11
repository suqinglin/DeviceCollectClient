package com.nexless.ccommble.conn

import android.bluetooth.*
import com.nexless.ccommble.util.CommLog
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * 蓝牙连接，通知，数据等回调
 */
class BluetoothConnectionCallback(devName: String, endIdentify: Array<String>?, listener: BluetoothListener) : BluetoothGattCallback() {
    val TAG = this.javaClass.simpleName
    private var devName: String = ""
    private var dataPackages: DataPackages? = null
    private var receiveData: ByteArray? = null
    private var endIdentify: Array<String>? = null
    private val bluetoothListener: BluetoothListener
    private var bluetoothStatus: AtomicInteger
    private var timerConnTimeout: Disposable? = null
    private var timerDiscoverServices: Disposable? = null
    private var timerNotify: Disposable? = null
    private var timerWriteTimeout: Disposable? = null
    private var timerReadTimeout: Disposable? = null
    private var bluetoothGatt: BluetoothGatt? = null
    private var bluetoothGattService: BluetoothGattService? = null
    private var bluetoothGattCharacteristicRead: BluetoothGattCharacteristic? = null
    private var bluetoothGattCharacteristicWrite: BluetoothGattCharacteristic? = null

    init {
        this.devName = devName
        this.endIdentify = endIdentify
        bluetoothListener = listener
        bluetoothStatus = AtomicInteger(ConnectionConstants.STATUS_CONN_START)
        timerConnTimeout = Observable.timer(5000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    bluetoothListener.onConnStatusFail(ConnectionConstants.STATUS_CONN_TIMEOUT)
                }
    }

    override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
        super.onConnectionStateChange(gatt, status, newState)
        CommLog.logE(TAG, "onConnectionStateChange status = $status newState = $newState")
        if (bluetoothStatus.get() == ConnectionConstants.STATUS_CONN_START && timerConnTimeout != null && !timerConnTimeout!!.isDisposed) {
            timerConnTimeout!!.dispose()
            timerConnTimeout = null
        }
        if (status == BluetoothGatt.GATT_SUCCESS) {
            CommLog.logE(TAG, "onConnectionStateChange->GATT_SUCCESS")
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                bluetoothStatus.set(ConnectionConstants.STATUS_CONN_SUCCESS)
                bluetoothListener.onConnStatusSucc(ConnectionConstants.STATUS_CONN_SUCCESS)
                if (gatt != null) {
                    bluetoothGatt = gatt
                    val isDiscoverServices = bluetoothGatt?.discoverServices()
                    if (isDiscoverServices!!) {
                        timerDiscoverServices = Observable.timer(5000, TimeUnit.MILLISECONDS)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    CommLog.logE(TAG, "closeGatt 6")
                                    closeGatt()
                                    bluetoothStatus.set(ConnectionConstants.STATUS_CONN_DISCOVERSERVICES_FAIL)
                                    bluetoothListener.onConnStatusFail(ConnectionConstants.STATUS_CONN_DISCOVERSERVICES_FAIL)
                                    CommLog.logE(TAG, "timerDiscoverServices=5000")
                                }
                    } else {
                        CommLog.logE(TAG, "onConnectionStateChange->isDiscoverServices = false")
                    }
                } else {
                    CommLog.logE(TAG, "closeGatt 3")
                    closeGatt()
                    bluetoothStatus.set(ConnectionConstants.STATUS_CONN_DISCOVERSERVICES_FAIL)
                    bluetoothListener.onConnStatusFail(ConnectionConstants.STATUS_CONN_DISCOVERSERVICES_FAIL)
                    CommLog.logE(TAG, "gatt=null")
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                receiveData = null
                CommLog.logE(TAG, "closeGatt 4")
                closeGatt()
                if (bluetoothStatus.get() == ConnectionConstants.STATUS_CONN_ENNOTIFY_SUCC) {
                    bluetoothStatus.set(ConnectionConstants.STATUS_CONN_DISCONN)
                    bluetoothListener.onConnStatusSucc(ConnectionConstants.STATUS_CONN_DISCONN)
                } else {
                    bluetoothListener.onConnStatusFail(ConnectionConstants.STATUS_CONN_FAIL)
                }
                CommLog.logE(TAG, "onConnectionStateChange->STATE_DISCONNECTED")
            }
        } else {
            receiveData = null
            CommLog.logE(TAG, "onConnectionStateChange->STATUS_CONN_FAIL")
//            if (bluetoothStatus.get() != ConnectionConstants.STATUS_DATA_WRITE_SUCC) {
            CommLog.logE(TAG, "closeGatt 5")
                closeGatt()
                bluetoothStatus.set(ConnectionConstants.STATUS_CONN_FAIL)
                bluetoothListener.onConnStatusFail(ConnectionConstants.STATUS_CONN_FAIL)
//            }
        }
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
        super.onServicesDiscovered(gatt, status)
        CommLog.logE(TAG, "onServicesDiscovered status = $status")
        if (bluetoothStatus.get() == ConnectionConstants.STATUS_CONN_SUCCESS && timerDiscoverServices != null && !timerDiscoverServices!!.isDisposed) {
            timerDiscoverServices!!.dispose()
            timerDiscoverServices = null
        }
        bluetoothGattService = bluetoothGatt!!.getService(UUID.fromString(UUID_SERVICE))
        if (bluetoothGattService != null) {
            val singleChar = bluetoothGattService!!.getCharacteristic(UUID.fromString(UUID_SINGLE))
            if (singleChar != null) {
                bluetoothGattCharacteristicRead = singleChar
                bluetoothGattCharacteristicWrite = singleChar
            } else {
                bluetoothGattCharacteristicRead = bluetoothGattService!!.getCharacteristic(UUID.fromString(UUID_READ))
                bluetoothGattCharacteristicWrite = bluetoothGattService!!.getCharacteristic(UUID.fromString(UUID_WRITE))
            }
            if (bluetoothGattCharacteristicRead != null && bluetoothGattCharacteristicWrite != null) {
                val descriptor = bluetoothGattCharacteristicRead!!.getDescriptor(UUID.fromString(UUID_DESCRIPTION))
                if (descriptor != null) {
                    bluetoothStatus.compareAndSet(ConnectionConstants.STATUS_CONN_SUCCESS, ConnectionConstants.STATUS_CONN_DISCOVERSERVICES_SUCC)
                    bluetoothListener.onConnStatusSucc(ConnectionConstants.STATUS_CONN_DISCOVERSERVICES_SUCC)
                    bluetoothGatt!!.setCharacteristicNotification(bluetoothGattCharacteristicRead, true)
                    descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    val isWriteNotification = bluetoothGatt!!.writeDescriptor(descriptor)
                    if (!isWriteNotification) {
                        timerNotify = Observable.timer(5000, TimeUnit.MILLISECONDS)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    bluetoothStatus.set(ConnectionConstants.STATUS_CONN_ENNOTIFY_FAIL)
                                    bluetoothListener.onConnStatusFail(ConnectionConstants.STATUS_CONN_ENNOTIFY_FAIL)
                                }
                    }
                } else {
                    CommLog.logE(TAG, "closeGatt 2")
                    closeGatt()
                    bluetoothStatus.set(ConnectionConstants.STATUS_CONN_DISCOVERSERVICES_FAIL)
                    bluetoothListener.onConnStatusFail(ConnectionConstants.STATUS_CONN_DISCOVERSERVICES_FAIL)
                    CommLog.logE(TAG, "BluetoothConnectionCallback：descriptor=null")
                }
            } else {
                gatt!!.refreshGattCache()
                CommLog.logE(TAG, "closeGatt 1")
                closeGatt()
                bluetoothStatus.set(ConnectionConstants.STATUS_CONN_DISCOVERSERVICES_FAIL)
                bluetoothListener.onConnStatusFail(ConnectionConstants.STATUS_CONN_DISCOVERSERVICES_FAIL)
                CommLog.logE(TAG, "BluetoothConnectionCallback：bluetoothGattCharacteristicWrite=null")
            }
        } else {
            CommLog.logE(TAG, "closeGatt 0")
            closeGatt()
            bluetoothStatus.set(ConnectionConstants.STATUS_CONN_DISCOVERSERVICES_FAIL)
            bluetoothListener.onConnStatusFail(ConnectionConstants.STATUS_CONN_DISCOVERSERVICES_FAIL)
            CommLog.logE(TAG, "BluetoothConnectionCallback：bluetoothGattService=null")
        }
    }

    override fun onDescriptorWrite(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
        super.onDescriptorWrite(gatt, descriptor, status)
        CommLog.logE(TAG, "onDescriptorWrite status = $status")
        if (bluetoothStatus.get() == ConnectionConstants.STATUS_CONN_DISCOVERSERVICES_SUCC && timerNotify != null && !timerNotify!!.isDisposed) {
            timerNotify!!.dispose()
            timerNotify = null
        }
        if (status == 0) {
            bluetoothStatus.set(ConnectionConstants.STATUS_CONN_ENNOTIFY_SUCC)
            bluetoothListener.onConnStatusSucc(ConnectionConstants.STATUS_CONN_ENNOTIFY_SUCC)
            if (dataPackages != null && dataPackages!!.hasNext()) {
                writeFirstPageData()
            }
        } else {
            bluetoothStatus.set(ConnectionConstants.STATUS_CONN_ENNOTIFY_FAIL)
            bluetoothListener.onConnStatusFail(ConnectionConstants.STATUS_CONN_ENNOTIFY_FAIL)
        }
    }

    override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
        super.onCharacteristicWrite(gatt, characteristic, status)
        CommLog.logE(TAG, "${bluetoothStatus.get()} onCharacteristicWrite status = $status")
        closeTimerWriteTimeout()
        if (status == 0) {
            if (dataPackages != null && dataPackages!!.hasNext()) {
                val data = dataPackages!!.next()
                bluetoothGattCharacteristicWrite?.writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
                bluetoothGattCharacteristicWrite?.value = data
                val isWriteCharacteristic = bluetoothGatt?.writeCharacteristic(bluetoothGattCharacteristicWrite)
                if (!isWriteCharacteristic!!) {
                    timerWriteTimeout = Observable.timer(10000, TimeUnit.MILLISECONDS)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                bluetoothListener.onConnStatusFail(ConnectionConstants.STATUS_DATA_WRITE_FAIL)
                                dataPackages = null
                                receiveData = null
                            }
                }
                CommLog.logE(TAG, "${bluetoothStatus.get()} onCharacteristicWrite status = $status data = ${data.size}")
            } else {
                timerReadTimeout = Observable.timer(5000, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            bluetoothListener.onConnStatusFail(ConnectionConstants.STATUS_DATA_READ_TIMEOUT)
                        }
                bluetoothStatus.set(ConnectionConstants.STATUS_DATA_WRITE_SUCC)
            }
        } else {
            bluetoothListener.onConnStatusFail(ConnectionConstants.STATUS_DATA_WRITE_FAIL)
        }
    }

    override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
        super.onCharacteristicChanged(gatt, characteristic)
        closeTimerReadTimeout()
        if (bluetoothStatus.compareAndSet(ConnectionConstants.STATUS_DATA_WRITE_SUCC, ConnectionConstants.STATUS_DATA_READING)) {
            receiveData = null
        }
//        if (bluetoothStatus.get() == ConnectionConstants.STATUS_DATA_READ_COMPLTED) {
//            return
//        }
        val temp = characteristic!!.value
        CommLog.logE(TAG, "${bluetoothStatus.get()} onCharacteristicChanged temp:${String(temp)}")

        receiveData = if (receiveData == null) {
            temp
        } else {
            mergeByte(receiveData!!, temp)
        }

        if (receiveEnd()) {
//            bluetoothStatus.set(ConnectionConstants.STATUS_DATA_READ_COMPLTED)
            bluetoothListener.onDataChange(receiveData)
            bluetoothStatus.set(ConnectionConstants.STATUS_CONN_ENNOTIFY_SUCC)
            receiveData = null
        }
    }

    private fun receiveEnd(): Boolean {
        return String(receiveData!!).contains("\r\n")
    }

    fun setCanReceiveData() {
        bluetoothStatus.set(ConnectionConstants.STATUS_DATA_WRITE_SUCC)
//        timerReadTimeout = Observable.timer(10000, TimeUnit.MILLISECONDS)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe {
//                    bluetoothListener.onConnStatusFail(ConnectionConstants.STATUS_DATA_READ_TIMEOUT)
//                }
    }

    private fun mergeByte(b1: ByteArray, b2: ByteArray): ByteArray {
        val b3 = ByteArray(b1.size + b2.size)
        System.arraycopy(b1, 0, b3, 0, b1.size)
        System.arraycopy(b2, 0, b3, b1.size, b2.size)
        return b3
    }

    fun closeTimerWriteTimeout() {
        if (timerWriteTimeout != null && !timerWriteTimeout!!.isDisposed) {
            timerWriteTimeout!!.dispose()
            timerWriteTimeout = null
        }
    }

    fun closeTimerReadTimeout() {
        if (timerReadTimeout != null && !timerReadTimeout!!.isDisposed) {
            timerReadTimeout!!.dispose()
            timerReadTimeout = null
        }
    }

    private fun writeFirstPageData() {
        if (dataPackages != null && dataPackages!!.hasNext()) {
            val data = dataPackages!!.next()
            receiveData = null
//            timerWriteTimeout = Observable.timer(10000, TimeUnit.MILLISECONDS)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe {
//                        bluetoothListener.onConnStatusFail(ConnectionConstants.STATUS_DATA_WRITE_FAIL)
//                        dataPackages = null
//                        receiveData = null
//                    }
            bluetoothGattCharacteristicWrite?.writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
            bluetoothGattCharacteristicWrite?.value = data
            bluetoothGatt?.writeCharacteristic(bluetoothGattCharacteristicWrite)
            CommLog.logE(TAG, "writeFirstPageData data = ${data.size} dataPackages = ${dataPackages.toString().length}")
        } else {
            bluetoothListener.onConnStatusFail(ConnectionConstants.STATUS_DATA_WRITE_FAIL)
            CommLog.logE(TAG, "writeFirstPageData data is null or don't has next")
        }
    }

    fun writeData(dataPackages: DataPackages, endIdentify: Array<String>?) {
        CommLog.logE(TAG, "writeData bluetoothStatus = " + bluetoothStatus.get())
        this.dataPackages = dataPackages
        this.endIdentify = endIdentify
        if (bluetoothStatus.get() == ConnectionConstants.STATUS_CONN_ENNOTIFY_SUCC) {
            writeFirstPageData()
        }
    }

    /**
     * 刷新缓存
     */
    fun BluetoothGatt.refreshGattCache(): Boolean = try {
        val refresh = BluetoothGatt::class.java.getMethod("refresh")
        refresh?.let {
            refresh.isAccessible = true
            refresh.invoke(this, *(arrayOfNulls<Any>(0)))
            true
        } ?: false
    } catch (e: Exception) {
        false
    }

    fun disConnGatt() {
        if (bluetoothGatt != null) {
            CommLog.logE(TAG, "disconnect")
            bluetoothGatt!!.disconnect()
        }
    }

    fun closeGatt() {
        if (bluetoothGatt != null) {
            bluetoothGatt!!.close()
//            val isRefreshSuccess = bluetoothGatt!!.refreshGattCache()
//            logI("close isRefreshSuccess:$isRefreshSuccess")
            CommLog.logE(TAG, "close")
            bluetoothGatt = null
            bluetoothGattService = null
        }
    }
}