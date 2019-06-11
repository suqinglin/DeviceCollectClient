package com.nexless.ccommble.data;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 顺丰箱包锁加密解密工具类
 * Created by wangkun23 on 2019/5/3.
 */
public class BagLockAESUtils {

    /**
     * 顺丰箱包锁加密规则
     *
     * @return
     */
    public static byte[] encrypt(byte[] byteContent, byte[] bytePassword) {
        try {
            SecretKeySpec key = new SecretKeySpec(bytePassword, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(byteContent);
        } catch (NoSuchPaddingException
                | NoSuchAlgorithmException
                | IllegalBlockSizeException
                | BadPaddingException
                | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 顺丰箱包锁解密规则
     *
     * @return
     */
    public static byte[] decrypt(byte[] byteContent, byte[] bytePassword) {
        try {
            SecretKeySpec key = new SecretKeySpec(bytePassword, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] result = cipher.doFinal(byteContent);
            return result;
        } catch (NoSuchPaddingException
                | NoSuchAlgorithmException
                | IllegalBlockSizeException
                | BadPaddingException
                | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * crc16 多项式校验方法
     *
     * @param buffer
     * @return
     */
    public static int crc16(int base, final byte[] buffer) {
        int crc = base;
        for (byte buf : buffer) {
            int i = buf;
            i &= 0xFF;
            crc = (((crc >> 8) & 0xFF) | (crc << 8));
            crc ^= i;
            crc ^= ((crc & 0xFF) >> 4) & 0xFF;
            crc ^= (crc << 8) << 4;
            crc ^= ((crc & 0xFF) << 4) << 1;
            crc &= 0xffff;
        }
        crc &= 0xffff;
        return crc;
    }
}
