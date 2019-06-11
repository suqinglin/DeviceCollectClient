package com.nexless.ccommble.data;

import com.nexless.ccommble.data.model.LockResult;

import com.nexless.ccommble.codec.DecoderException;
import com.nexless.ccommble.codec.binary.Hex;
import com.nexless.ccommble.data.model.LogResult;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 箱包锁的普通参数组装SDK
 * Created by wangkun23 on 2019/5/3.
 */
public class BaglockUtils {
    /**
     * 获取aes加密秘钥
     *
     * @param mac     mac地址
     * @param bagSN   箱包锁编号(流水号)
     * @param userKey 商户唯一编号
     */
    public static byte[] getAppKey(String mac, Integer bagSN, String userKey) throws DecoderException {
        if (userKey.length() > 14) {
            throw new IllegalArgumentException("userKey length must lt 14.");
        }
        byte[] buf = userKey.getBytes(StandardCharsets.US_ASCII);
        int base0 = BagLockAESUtils.crc16(0, Hex.decodeHex(mac));
        /**
         * 把十六进制反转过来 计算CRC 保持跟C语言逻辑一直
         */
        // byte 3 to byte 0
        int reverseHex = ((bagSN >> 24) & 0xff) |
                // byte 1 to byte 2
                ((bagSN << 8) & 0xff0000) |
                // byte 2 to byte 1
                ((bagSN >> 8) & 0xff00) |
                // byte 0 to byte 3
                ((bagSN << 24) & 0xff000000);
        String snHex = String.format("%08X",reverseHex);
        int crcResult = BagLockAESUtils.crc16(base0, Hex.decodeHex(snHex));
        return ByteBuffer.allocate(16)
                .put(buf)
                .putShort((short) crcResult)
                .array();
    }

    /**
     * 生产开锁的秘钥信息
     *
     * @param rollCode  滚动码
     * @param timestamp 时间戳
     * @param userId    用户id
     * @return
     */
    public static byte[] getCipher(Integer rollCode, Integer timestamp, String userId) {
        if (userId.length() > 8) {
            throw new IllegalArgumentException("userId length must lt 8.");
        }
        byte[] buf = userId.getBytes(StandardCharsets.US_ASCII);
        return ByteBuffer.allocate(16)
                .putInt(rollCode)
                .putInt(timestamp)
                .put(buf)
                .array();
    }

    /**
     * 读取蓝牙日志的请求数据
     * @param startTime 开始时间秒数
     * @return
     */
    public static byte[] getReadLogBuf(long startTime) {
        byte[] startTimeBuf = ByteBuffer.allocate(4).putInt((int) startTime).array();

        ByteBuffer readLogBuf = ByteBuffer
                .allocate(16)
                .put(startTimeBuf);
        // 补12个字节的0x00占位
        for(int i = 0; i < 12; i++) {
            readLogBuf.put((byte) 0x00);
        }
        return readLogBuf.array();
    }

    /**
     * 解析蓝牙返回的数据
     * <p>具体协议规则请参考协议文档</p>
     *
     * @param lockData
     * @return
     */
    public static LockResult parseLockResult(String lockData) throws DecoderException {
        // 不验证crc
        byte[] buffer = Hex.decodeHex(lockData);
        String cmd = String.format("0x%x", buffer[0]);
        String result = String.format("0x%x", buffer[1]);
        Short battery = ByteBuffer.wrap(Arrays.copyOfRange(buffer, 2, 4)).getShort();
        int sn = ByteBuffer.wrap(Arrays.copyOfRange(buffer, 4, 8)).getInt();
        return new LockResult(cmd, result, battery, sn);
    }

    /**
     * 解析从蓝牙读取到的日志
     * @param logData 蓝牙返回的原始数据
     * @return 解析的日志对象
     * @throws DecoderException
     */
    public static LogResult parseLogResult(String logData) throws DecoderException {
        // 未验证crc
        byte[] buffer = Hex.decodeHex(logData);
        String cmd = String.format("0x%x", buffer[0]);
        String result = String.format("0x%x", buffer[1]);
        String userId = Hex.encodeHexString(Arrays.copyOfRange(buffer, 2, 10));
        long timeStamp = Long.valueOf(Hex.encodeHexString(Arrays.copyOfRange(buffer, 10, 14)), 16);
        long sn = Long.valueOf(Hex.encodeHexString(Arrays.copyOfRange(buffer, 14, 18)), 16);
        return new LogResult(cmd, result, userId, timeStamp, sn);
    }
}
