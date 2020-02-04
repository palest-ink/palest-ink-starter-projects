package com.palest.ink.zk.integration.listener;

import com.alibaba.fastjson.JSON;
import com.palest.ink.zk.event.AfterUpdate;
import com.palest.ink.zk.event.ZkEventData;
import com.palest.ink.zk.event.ZkEventType;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通过注解的反射调用的适配器
 *
 * @author WeiHui
 * @date 2019/1/21
 */
public abstract class AbstractPathNodeDataConsumer implements PathNodeDataConsumer {

	/**
	 * 事件对应的方法缓存
	 */
	private Map<ZkEventType, List<Method>> methodMap = new ConcurrentHashMap<>();

	/**
	 * 构造方法初始化-注解对应的方法，并加入缓存
	 */
	public AbstractPathNodeDataConsumer() {
		initMethodCache();
	}

	/**
	 * 方法缓存初始化
	 */
	private void initMethodCache() {
		Method[] methods = this.getClass().getMethods();
		if (ArrayUtils.isEmpty(methods)) {
			return;
		}
		for (Method method : methods) {
			AfterUpdate afterUpdate = method.getAnnotation(AfterUpdate.class);
			if (afterUpdate != null) {
				ZkEventType[] eventTypes = afterUpdate.value();
				if (ArrayUtils.isNotEmpty(eventTypes)) {
					Arrays.stream(eventTypes).forEach(k -> {
						methodMap.computeIfAbsent(k, v -> new LinkedList<>());
						methodMap.get(k).add(method);
					});
				}
			}
		}
	}

	/**
	 * 消费
	 *
	 * @param eventData 事件数据
	 */
	@Override
	public final void consume(ZkEventData eventData) {
		List<Method> methods = methodMap.get(eventData.getEventType());
		if (!CollectionUtils.isEmpty(methods)) {
			methods.forEach(m -> ReflectionUtils.invokeMethod(m, this, extractMessage(m, eventData)));
		}
	}

	/**
	 * 将string 封装成method 想要的参数类型
	 *
	 * @param eventData 原始数据
	 * @param method    方法
	 * @return 目标参数
	 */
	protected Object[] extractMessage(Method method, ZkEventData eventData) {
		Class<?>[] types = method.getParameterTypes();
		if (types.length == 1 && types[0].isInstance(ZkEventData.class)) {
			return new Object[]{eventData};
		} else if (types.length == 1 && types[0].isInstance(String.class)) {
			return new Object[]{eventData.getData()};
		} else if ((types.length == 1 && !types[0].isInstance(String.class))) {
			return new Object[]{JSON.parseObject(eventData.getData(), types[0])};
		} else {
			return null;
		}
	}

}
