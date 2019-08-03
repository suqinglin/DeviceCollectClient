package com.nexless.ccommble.data;

import com.nexless.ccommble.codec.DecoderException;
import com.nexless.ccommble.codec.binary.Hex;
import com.nexless.ccommble.util.CommLog;
import com.nexless.ccommble.util.CommUtil;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @date: 2019/5/6
 * @author: su qinglin
 * @description:
 */
public class Encrypt {

    public static byte[] setUserKeyEncrypt(String userKey) {

        byte[] userKeyBuf = ByteBuffer.allocate(16)
                .put(userKey.getBytes(StandardCharsets.US_ASCII))
                .put((byte) 0x00)
                .put((byte) 0x00)
                .array();
        int crc = BagLockAESUtils.crc16(0, userKeyBuf);
        byte[] crcBuf = ByteBuffer.allocate(2).putShort((short) crc).array();
        return ByteBuffer.allocate(20)
                .put((byte) 0x40)
                .put(userKeyBuf)
                .put((byte) 0x00)
                .put(crcBuf)
                .array();
    }

    public static byte[] setSnCntEncrypt(long sn, long cnt, String mac) throws DecoderException {

        byte[] buf = ByteBuffer.allocate(14)
                .put(CommUtil.long2Bytes(cnt))
                .put(CommUtil.long2Bytes(sn))
                .put(Hex.decodeHex(mac))
                .array();
        int crc = BagLockAESUtils.crc16(0, buf);
        byte[] crcBuf = ByteBuffer.allocate(2).putShort((short) crc).array();
        return ByteBuffer.allocate(20)
                .put((byte) 0x41)
                .put(buf)
                .put((byte) 0x00)
                .put((byte) 0x00)
                .put((byte) 0x00)
                .put(crcBuf)
                .array();
    }

    public static byte[] openEncrypt(long sn, long cnt, long timestamp, String mac, String
            userId, String userKey) throws DecoderException {

        byte[] appKeyBuf = BaglockUtils.getAppKey(mac, (int) sn, userKey);
        CommLog.logE("appKey:" + Hex.encodeHexString(appKeyBuf).toUpperCase());
        byte[] cipherBuf = BaglockUtils.getCipher((int) cnt, (int) timestamp, userId);
        int crc = BagLockAESUtils.crc16(0, cipherBuf);
        byte[] crcBuf = ByteBuffer.allocate(2).putShort((short) crc).array();
        byte[] encrypt = BagLockAESUtils.encrypt(cipherBuf, appKeyBuf);
        return ByteBuffer.allocate(20)
                .put((byte) 0x02)
                .put(encrypt)
                .put((byte) 0x00)
                .put(crcBuf)
                .array();
    }

    public static byte[] readLogEncrypt(long sn, long timeStamp, String mac, String userKey) throws DecoderException {
        byte[] appKeyBuf = BaglockUtils.getAppKey(mac, (int) sn, userKey);
        byte[] readLogBuf = BaglockUtils.getReadLogBuf(timeStamp);
        int crc = BagLockAESUtils.crc16(0, readLogBuf);
        byte[] crcBuf = ByteBuffer.allocate(2).putShort((short) crc).array();
        byte[] encrypt = BagLockAESUtils.encrypt(readLogBuf, appKeyBuf);
        return ByteBuffer.allocate(20)
                .put((byte) 0x10)
                .put(encrypt)
                .put((byte) 0x00)
                .put(crcBuf)
                .array();
    }

    public static byte[] sendCommandEncrypt(String cmd) {
//        cmd = cmd + " ";
        byte[] buf = cmd.getBytes(StandardCharsets.US_ASCII);
        byte[] endBuf = new byte[]{0x0D};
        return ByteBuffer.allocate(buf.length + 1)
                .put(buf)
                .put(endBuf)
                .array();
    }

    public static void main(String[] args) {
        System.out.println(Hex.encodeHexString(sendCommandEncrypt("PrdClr")));
    }
}
