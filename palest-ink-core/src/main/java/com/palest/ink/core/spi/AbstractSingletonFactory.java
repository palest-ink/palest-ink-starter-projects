package com.palest.ink.core.spi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author WeiHui
 * @date 2019/1/22
 */
public abstract class AbstractSingletonFactory<K, V> implements SingletonFactory<K, V> {

	/**
	 * logger
	 */
	protected final Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * 抽象策略工厂
	 *
	 * maps ,k-策略名称 , v-> 具体的实现
	 */
	private Map<K, V> factory = new ConcurrentHashMap<>();

	/**
	 * 注册不同路由实现
	 */
	@Override
	public Optional<V> registerSingleFactory(K key, V value) {
		Optional<V> optional = Optional.ofNullable(factory.put(key, value));
		if (optional.isPresent()) {
			log.info("覆盖实现:{}-{}", key, value);
		} else {
			log.info("注册实现:{}-{}", key, value);
		}
		return optional;
	}

	/**
	 * 移除不同路由实现
	 */
	@Override
	public Optional<V> unRegisterSingleFactory(K key) {
		Optional<V> optional = Optional.ofNullable(factory.remove(key));
		optional.ifPresent(v -> log.info("移除实现:{}", key));
		return optional;
	}

	/**
	 * 注册所有路由实现
	 */
	@Override
	public void registerAllFactory(Map<K, V> factory) {
		factory.forEach(factory::put);
	}

	/**
	 * 获取单个实现
	 *
	 * @return 所有实现
	 */
	@Override
	public Optional<V> getFactory(K key) {
		return key != null ? Optional.ofNullable(factory.get(key)) : Optional.empty();
	}

	/**
	 * 获取所有实现
	 *
	 * @return 所有实现
	 */
	@Override
	public Map<K, V> getAllFactory() {
		return Collections.unmodifiableMap(factory);
	}

	/**
	 * 把所有的钥匙
	 *
	 * @return {@link Set<K>}
	 */
	@Override
	public Set<K> getAllKeys() {
		return Collections.unmodifiableSet(factory.keySet());
	}

	/**
	 * 得到所有的值
	 *
	 * @return {@link Collection<V>}
	 */
	@Override
	public Collection<V> getAllValues() {
		return Collections.unmodifiableCollection(factory.values());
	}

}
