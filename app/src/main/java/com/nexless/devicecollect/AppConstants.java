package com.nexless.devicecollect;

/**
 * @date: 2019/6/5
 * @author: su qinglin
 * @description:
 */
public class AppConstants {
    public static final boolean DEBUG = true;
//    public static final String SERVER_URL = "http://183.66.233.86:8089/";
    public static final String SERVER_URL = "http://120.92.114.123:8089/";
    public static final String APPPREFERENCE_FILE_NAME = "DeviceCollect";

    public static final String RESPONSE_CODE_SUCCESS = "200";
    public static final String RESPONSE_CODE_NOT_LOGIN = "1004007";
    public static final String RESPONSE_CODE_DEVICE_NOT_EXIST = "1006003";
    public static final String RESPONSE_CODE_USERNAME_PWD_INCORRECT = "1000004";
    public static final String EXTRA_DEVICE = "extra.device";
    public static final String EXTRA_MANUF_ID = "extra.manuf.id";
    public static final String EXTRA_TOOL_ID = "extra.tool.id";
    public static final String EXTRA_MANUF = "extra.manuf";
    public static final String EXTRA_IS_SELECT_TOOL = "extra.isselecttool";

    /******************************** 蓝牙广播UUID ********************************/
    public static final String BLE_BROADCAST_UUID = "55555555";
    /******************************** 请求命令：APP -> 设备 *****************************/
    public static final String CMD_REQ_PAIR = "PrdPair 00000001";
    public static final String CMD_REQ_DOOR = "PrdDoor 00000001";
    public static final String CMD_REQ_INS_FP = "PrdInsFp";
    public static final String CMD_REQ_TOUCH = "PrdTouch";
    public static final String CMD_REQ_SLEEP = "PrdSleep";
    public static final String CMD_REQ_DEL = "PrdDel";
    public static final String CMD_REQ_FA = "PrdTFa";
    public static final String CMD_REQ_BURN = "PrdBurn";

    /******************************** 响应命令：设备 -> APP *****************************/
    public static final String CMD_ACK_VOLTAGE = "#PrdAckVol";
    public static final String CMD_ACK_PAIR_OK = "#PrdAckPairOK";
    public static final String CMD_ACK_PAIR_ERROR = "#PrdAckPairError";
    public static final String CMD_ACK_DOOR = "#PrdAckDoorOK";
    public static final String CMD_ACK_TOUCH_OK = "#PrdAckTouchOK";
    public static final String CMD_ACK_TOUCH_NUM = "#PrdAckTouch:";
    public static final String CMD_ACK_SLEEP = "#PrdAckSleepOK";
    public static final String CMD_ACK_DEL = "#PrdAckDelOK";
    public static final String CMD_ACK_INS_FP = "#PrdAckInsFpOK";
    public static final String CMD_ACK_FP_SUCC = "#PrdAckFpSuccOK";
    public static final String CMD_ACK_FA_START = "#PrdAckTFaOK";
    public static final String CMD_ACK_FA_END = "#PrdAckLoraOK";
    public static final String CMD_ACK_BURN = "#PrdAckBurnOK";
}
