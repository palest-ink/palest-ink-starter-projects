package com.palest.ink.core.spi;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author WeiHui
 * @date 2019/3/19 16:08
 * @since JDK 1.8
 */
public interface SingletonFactory<K, V> {

	/**
	 * 注册不同路由实现
	 *
	 * @param key   key
	 * @param value value
	 */
	Optional<V> registerSingleFactory(K key, V value);

	/**
	 * 移除不同路由实现
	 *
	 * @param key key
	 * @return value
	 */
	Optional<V> unRegisterSingleFactory(K key);

	/**
	 * 注册所有路由实现
	 *
	 * @param factory factory
	 */
	void registerAllFactory(Map<K, V> factory);

	/**
	 * 获取单个实现
	 *
	 * @param key key
	 * @return value
	 */
	Optional<V> getFactory(K key);

	/**
	 * 获取所有实现
	 *
	 * @return 所有实现
	 */
	Map<K, V> getAllFactory();

	/**
	 * 获取所有实现
	 *
	 * @return 所有实现
	 */
	Set<K> getAllKeys();

	/**
	 * 获取所有实现
	 *
	 * @return 所有实现
	 */
	Collection<V> getAllValues();

}
