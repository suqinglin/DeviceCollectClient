package com.nexless.devicecollect.util;

import com.nexless.ccommble.codec.DecoderException;
import com.nexless.ccommble.codec.binary.Hex;
import com.nexless.ccommble.conn.BluetoothListener;
import com.nexless.ccommble.conn.ConnectionHelper;
import com.nexless.ccommble.data.BaglockUtils;
import com.nexless.ccommble.data.Encrypt;
import com.nexless.ccommble.data.model.LockResult;
import com.nexless.ccommble.util.BleStatusUtil;
import com.nexless.ccommble.util.CommLog;
import com.nexless.ccommble.util.CommUtil;

import org.jetbrains.annotations.Nullable;

/**
 * @date: 2019/6/10
 * @author: su qinglin
 * @description:
 */
public class BluetoothUtil {

    private String mac;
    private SendCmdCallBack cmdCallBack;

    public BluetoothUtil(String mac) {
        this.mac = mac;
    }

    public void sendCommand(String cmd, boolean hasPrdAck, SendCmdCallBack callBack) {
        cmdCallBack = callBack;
        byte[] sendData = Encrypt.sendCommandEncrypt(cmd);
        CommLog.logE("BluetoothUtil", "sendData:" + Hex.encodeHexString(sendData).toUpperCase());
        ConnectionHelper.getInstance().bleCommunication(
                mac,
                mac,
                null,
                hasPrdAck,
                sendData,
                true,
                new BluetoothListener() {

                    @Override
                    public void onDataChange(@Nullable byte[] data) {
                        if (cmdCallBack != null) {
                            cmdCallBack.onSuccess(new String(data));
                        }
                    }

                    @Override
                    public void onConnStatusFail(int status) {
                        if (cmdCallBack != null) {
                            callBack.onFailure(BleStatusUtil.getConnectStatusMsg(status));
                        }
                    }

                    @Override
                    public void onConnStatusSucc(int status) {

                    }
                }, 15000);
    }

    public void cancel() {
        cmdCallBack = null;
    }

    public interface SendCmdCallBack {
        void onSuccess(String result);
        void onFailure(String message);
    }
}
