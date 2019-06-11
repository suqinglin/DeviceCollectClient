package com.nexless.ccommble.conn

/**
* @auth create by calm
* @time 2018/8/18 15:38
* @note DataPackages
*/
class DataPackages(data:ByteArray): Iterator<ByteArray> {
    private val sendData:ByteArray
    private val dataLength:Int
    private var cursor:Int
    private var packageSize:Int
    init {
        sendData = data
        dataLength = sendData.size
        cursor = 0
        packageSize = 20
    }
    override fun hasNext(): Boolean {
        return cursor < dataLength
    }

    override fun next(): ByteArray {
        val srcPos = cursor
        if(srcPos + packageSize > dataLength){
            packageSize = dataLength - srcPos
        }
        val buf = ByteArray(packageSize)
        System.arraycopy(sendData,srcPos,buf,0,packageSize)
        cursor = srcPos + packageSize
        return buf
    }
}