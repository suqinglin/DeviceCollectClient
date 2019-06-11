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

    public BluetoothUtil(String mac) {
        this.mac = mac;
    }

    public void sendCommand(String cmd, SendCmdCallBack callBack) {
        byte[] sendData = Encrypt.sendCommandEncrypt(cmd);
        CommLog.logE("BluetoothUtil", "sendData:" + Hex.encodeHexString(sendData).toUpperCase());
        ConnectionHelper.getInstance().bleCommunication(
                mac,
                mac,
                null,
                sendData,
                true,
                new BluetoothListener() {

                    @Override
                    public void onDataChange(@Nullable byte[] data) {
                        callBack.onSuccess(new String(data).replace("\r\n", ""));
                    }

                    @Override
                    public void onConnStatusFail(int status) {
                        callBack.onFailure(BleStatusUtil.getConnectStatusMsg(status));
                    }

                    @Override
                    public void onConnStatusSucc(int status) {

                    }
                }, 3000);
    }

    public interface SendCmdCallBack {
        void onSuccess(String result);
        void onFailure(String message);
    }
}
