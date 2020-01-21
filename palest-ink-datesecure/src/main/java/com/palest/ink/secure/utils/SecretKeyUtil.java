package com.palest.ink.secure.utils;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;


/**
 * 类SecretKeyUtil.java的实现描述：秘钥管理工具类
 *
 * @author huanglifang Jun 1, 2017 2:20:22 PM
 */
@Slf4j
public class SecretKeyUtil {

	private static byte[] bsecretKey = null;

	/**
	 * 设置密钥
	 *
	 * @param key
	 */
	public static void setSecretKey(String key) {
		bsecretKey = Base64.decodeBase64(key.getBytes());
	}

	/**
	 * 得到密钥
	 */
	public static byte[] getSecretKey() {
		if (bsecretKey == null) {
			log.error("获取密钥失败,系统异常");
			throw new RuntimeException("密钥获取异常");
		}
		return bsecretKey;
	}

}
