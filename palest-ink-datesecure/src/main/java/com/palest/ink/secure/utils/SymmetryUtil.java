
package com.palest.ink.secure.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;

/**
 * 类SymmetryUtil.java的实现描述：数据加解密算法工具类
 * 
 * @author huanglifang May 23, 2017 4:26:38 PM
 */
@Slf4j
public class SymmetryUtil {
    private static byte[] initKey(byte[] b_key) {
        byte state[] = new byte[256];
        for (int i = 0; i < 256; i++) {
            state[i] = (byte) i;
        }
        int index1 = 0;
        int index2 = 0;
        if (b_key == null || b_key.length == 0) {
            return null;
        }
        for (int i = 0; i < 256; i++) {
            index2 = ((b_key[index1] & 0xff) + (state[i] & 0xff) + index2) & 0xff;
            byte tmp = state[i];
            state[i] = state[index2];
            state[index2] = tmp;
            index1 = (index1 + 1) % b_key.length;
        }
        return state;
    }

    private static byte[] RC4(byte[] input) {
        int x = 0;
        int y = 0;
        int xorIndex;
        byte[] result = new byte[input.length];
        byte[] key = initKey(SecretKeyUtil.getSecretKey());
        for (int i = 0; i < input.length; i++) {
            x = (x + 1) & 0xff;
            y = ((key[x] & 0xff) + y) & 0xff;
            byte tmp = key[x];
            key[x] = key[y];
            key[y] = tmp;
            xorIndex = ((key[x] & 0xff) + (key[y] & 0xff)) & 0xff;
            result[i] = (byte) (input[i] ^ key[xorIndex]);
        }
        return result;
    }

    /**
     * 加密
     * 
     * @param data 原始数据
     * @return
     */
    public static String encryption(String data) {
        if (StringUtils.isBlank(data)) {
            return data;
        }
        String encryptData = "";
        try {
            encryptData = new String(Base64.encodeBase64(RC4(data.getBytes("GBK"))));
        } catch (UnsupportedEncodingException e) {
            log.error("RC4 encry UnsupportedEncodingException {}", e);
        }
        return encryptData;
    }

    /**
     * 解密
     * 
     * @param data 加密后数据
     * @return
     */
    public static String decryption(String data) {
        if (StringUtils.isBlank(data)) {
            return data;
        }
        String decryptData = "";
        try {
            decryptData = new String(RC4(Base64.decodeBase64(data.getBytes())), "GBK");
        } catch (UnsupportedEncodingException e) {
            log.error("RC4 encry UnsupportedEncodingException {}", e);
        }
        return decryptData;
    }
}
