package com.nexless.ccommble.conn

import android.content.Context
import com.nexless.ccommble.util.CommLog
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
* @auth create by calm
* @time 2018/8/17 12:34
* @note ConnectionHelper
*/
class ConnectionHelper private constructor(){
    private val TAG = "BluetoothConnectionCallback"
    private var context:Context? = null
    /**
     * BluetoothConnection的MutableMap
     */
    private val connectionMap:MutableMap<String, BluetoothConnection> = mutableMapOf()
    /**
     * BluetoothListener的MutableMap
     */
    private val listenerMap:MutableMap<String, BluetoothListener> = mutableMapOf()
    private object BleApiHolder{
        val instance = ConnectionHelper()
    }
    companion object {
        @JvmStatic val instance: ConnectionHelper
            @Synchronized get() = BleApiHolder.instance
    }
    fun init(context: Context){
        this.context = context.applicationContext
    }

    /**
     * 获取蓝牙连接错误值
     * @param status Int 蓝牙连接错误码
     * @return String 蓝牙连接错误值
     */
    fun getConnectErrorStr(status: Int): String {
        return when (status) {
            in ConnectionConstants.STATUS_CONN_TIMEOUT..ConnectionConstants.STATUS_CONN_ENNOTIFY_FAIL -> "连接设备失败"
            ConnectionConstants.STATUS_DATA_WRITE_FAIL -> "写入数据失败"
            ConnectionConstants.STATUS_DATA_READ_TIMEOUT -> "读取数据超时"
            else -> "通讯失败"
        }
    }

    /**
     *
     * @param devName String 设备名称
     * @param mac String 设备mac地址
     * @param endIdentify Array<String>? 数据结束符
     * @param listener BluetoothListener 回调
     */
    fun connDevice(devName: String,mac: String,endIdentify:Array<String>?,listener: BluetoothListener){
        listenerMap[mac] = listener
        createConnection(devName,mac,endIdentify)
    }

    /**
     * 断开连接
     * @param mac String 设备mac地址
     */
    fun disConnDevice(mac: String){
        connectionMap[mac]?.disConnDevice()
        removeConncetion(mac)
    }

    /**
     *
     * @param mac String 设备mac地址
     * @return Boolean true：已连接，false：未连接
     */
    fun isConnected(mac: String):Boolean = connectionMap[mac] != null

    /**
     *
     * @param devName String String 设备名称
     * @param mac String 设备mac地址
     * @param endIdentify Array<String>? 数据结束符
     * @param data ByteArray 发送的数据
     * @param listener BluetoothListener 回调
     * @param delayDisConnTime Int 延迟断开时间，默认3s
     */
    fun bleCommunication(devName: String, mac: String, endIdentify: Array<String>?, data: ByteArray, reConnect: Boolean, listener: BluetoothListener,delayDisConnTime:Int = 3000){
        if(connectionMap[mac] == null){
            connDevice(devName,mac,endIdentify,listener)
        }else{
            listenerMap[mac] = listener
        }
        connectionMap[mac]?.sendData(delayDisConnTime,data, reConnect,endIdentify)
    }

    /**
     * 设置可继续接收数据
     * @param mac String 设备mac地址
     */
    fun setCanReceiveData(mac: String){
        connectionMap[mac]?.setCanReceiveData()
    }

    /**
     * 移除连接的缓存
     * @param mac String 设备mac地址
     */
    private fun removeConncetion(mac: String){
        connectionMap.remove(mac)
        listenerMap.remove(mac)
    }

    /**
     * 创建连接
     * @param devName String 设备名称
     * @param mac String 设备mac
     * @param endIdentify Array<String>? 结束符
     */
    private fun createConnection(devName: String,mac:String,endIdentify:Array<String>?){
        if(context == null){
            throw IllegalArgumentException("hava you init sdk ?")
        }
        if(connectionMap[mac] == null){
            val connection = BluetoothConnection(context!!,devName,mac,endIdentify,object: BluetoothClassListener(){
                override fun onConnStatusSucc(status: Int) {
                    super.onConnStatusSucc(status)
                    CommLog.logE(TAG, "onConnStatusSucc2 status = $status")
                    Observable.create(ObservableOnSubscribe<Int> {e -> e.onNext(status)})
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe{ i ->
                                listenerMap[mac]?.onConnStatusSucc(i)
                                if(i == ConnectionConstants.STATUS_CONN_DISCONN){
                                    removeConncetion(mac)
                                }
                            }
                }

                override fun onConnStatusFail(status: Int) {
                    super.onConnStatusFail(status)
                    CommLog.logE(TAG, "onConnStatusFail2 status = $status")
                    Observable.create(ObservableOnSubscribe<Int> {e -> e.onNext(status)})
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe{ i ->
                                listenerMap[mac]?.onConnStatusFail(i)
                                removeConncetion(mac)
                            }
                }

                override fun onDataChange(data: ByteArray?) {
                    super.onDataChange(data)
                    CommLog.logE(TAG, "onDataChange22->" + String(data!!))
                    Observable.create(ObservableOnSubscribe<ByteArray?> {e -> e.onNext(data!!)})
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe{ s ->
                                listenerMap[mac]?.onDataChange(s)
                            }
                }
            })
            connectionMap[mac] = connection
        }
    }
}