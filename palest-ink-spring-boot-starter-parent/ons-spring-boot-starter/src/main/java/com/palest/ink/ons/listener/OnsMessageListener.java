package com.palest.ink.ons.listener;

import com.aliyun.openservices.ons.api.Action;

import java.util.Map;

/**
 * ons消息监听
 *
 * @author August.Zhang
 * @version v1.0.0
 * @date 2020/1/22 17:12
 * @since JDK 1.8
 */
public interface OnsMessageListener<T> {

	/**
	 * 消费
	 *
	 * @param message        消息
	 * @param consumeContext 消费者配置的context信息
	 * @return {@link Action}
	 */
	Action consume(T message, Map<String, String> consumeContext);

}
