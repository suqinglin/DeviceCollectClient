package com.nexless.ccommble.conn

/**

 * @Author noti
 * @Date 2019/1/18-13:08
 * @Email 1026452140@qq.com
 */
object ConnectionConstants{

    //连接相关状态码
    /**
     * 初始
     */
    const val STATUS_CONN_START = 1001
    /**
     * 连接状态成功
     */
    const val STATUS_CONN_SUCCESS = 1002
    /**
     * 发现服务成功
     */
    const val STATUS_CONN_DISCOVERSERVICES_SUCC = 1003
    /**
     * 打开通知成功
     */
    const val STATUS_CONN_ENNOTIFY_SUCC = 1004
    /**
     * 断开连接
     */
    const val STATUS_CONN_DISCONN = 1005
    /**
     * 自动断开连接
     */
    const val STATUS_AUTO_DISCONN = 1006
    /**
     * 连接超时
     */
    const val STATUS_CONN_TIMEOUT = 2001
    /**
     * 连接状态失败
     */
    const val STATUS_CONN_FAIL = 2002
    /**
     * 发现服务失败
     */
    const val STATUS_CONN_DISCOVERSERVICES_FAIL = 2003
    /**
     * 打开通知失败
     */
    const val STATUS_CONN_ENNOTIFY_FAIL = 2004
    /**
     * 写数据失败
     */
    const val STATUS_DATA_WRITE_FAIL = 3001
    /**
     * 读数据超时
     */
    const val STATUS_DATA_READ_TIMEOUT = 3002
    /**
     * 写数据完成
     */
    const val STATUS_DATA_WRITE_SUCC = 4001
    /**
     * 开始读数据
     */
    const val STATUS_DATA_READING = 4002
    /**
     * 读数据完成
     */
    const val STATUS_DATA_READ_COMPLTED = 4003
}
//UUID
internal const val UUID_SERVICE_WECHAT = "0000fee7-0000-1000-8000-00805f9b34fb"
internal const val UUID_SERVICE = "000018f0-0000-1000-8000-00805f9b34fb"
internal const val UUID_READ = "00002af0-0000-1000-8000-00805f9b34fb"
internal const val UUID_WRITE = "00002af1-0000-1000-8000-00805f9b34fb"
internal const val UUID_DESCRIPTION = "00002902-0000-1000-8000-00805f9b34fb"
internal const val UUID_SINGLE = "00002afc-0000-1000-8000-00805f9b34fb"

//连接蓝牙超时时间
internal const val TIME_CONN_TIMEOUT = 6000