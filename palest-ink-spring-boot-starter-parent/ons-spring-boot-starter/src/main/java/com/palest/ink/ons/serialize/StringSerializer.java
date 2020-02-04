package com.palest.ink.ons.serialize;

import org.springframework.util.Assert;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 字符串序列化器
 *
 * @author August.zhang
 * @version v1.0.0
 * @date 2020/1/22 17:10
 * @since JDK 1.8
 */
public class StringSerializer implements Serializer<String> {

	/**
	 * 字符集
	 */
	private final Charset charset;

	/**
	 * 字符串序列化器
	 */
	public StringSerializer() {
		this(StandardCharsets.UTF_8);
	}

	/**
	 * 字符串序列化器
	 *
	 * @param charset 字符集
	 */
	public StringSerializer(Charset charset) {
		Assert.notNull(charset, "Charset must not be null!");
		this.charset = charset;
	}

	/**
	 * 反序列化
	 *
	 * @param bytes 字节
	 * @return {@link String}
	 */
	@Override
	public String deserialize(byte[] bytes) {
		return bytes == null ? null : new String(bytes, this.charset);
	}

	/**
	 * 序列化
	 *
	 * @param string 字符串
	 * @return {@link byte[]}
	 */
	@Override
	public byte[] serialize(String string) {
		return string == null ? null : string.getBytes(this.charset);
	}

}
