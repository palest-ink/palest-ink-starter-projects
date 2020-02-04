package com.palest.ink.ons.serialize;

public interface Serializer<T> {

	/**
	 * 序列化
	 * Serialize the given object to binary data.
	 *
	 * @param t object to serialize
	 * @return the equivalent binary data
	 * @throws SerializationException 序列化异常
	 */
	byte[] serialize(T t) throws SerializationException;

	/**
	 * 反序列化
	 * Deserialize an object from the given binary data.
	 *
	 * @param bytes object binary representation
	 * @return the equivalent object instance
	 * @throws SerializationException 序列化异常
	 */
	T deserialize(byte[] bytes) throws SerializationException;
}
