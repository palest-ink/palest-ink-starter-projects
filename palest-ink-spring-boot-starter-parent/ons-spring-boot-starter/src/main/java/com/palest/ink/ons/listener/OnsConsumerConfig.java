package com.palest.ink.ons.listener;

import lombok.Data;

import java.util.Map;

/**
 * @author August.Zhang
 * @version v1.0.0
 * @date 2020/1/22 17:52
 * @since JDK 1.8
 */
@Data
public class OnsConsumerConfig {

	/**
	 * uid
	 */
	private static final long serialVersionUID = 1244790913556856856L;

	/**
	 * The Topic.
	 */
	private String topic;

	/**
	 * The Consumer id.
	 */
	private String consumerId;

	/**
	 * The Accesskey.
	 */
	private String accesskey;

	/**
	 * The Secretkey.
	 */
	private String secretkey;

	/**
	 * The Onsaddr.
	 */
	private String onsAddr;

	/**
	 * The namesrvAddr.
	 */
	private String namesrvAddr;

	/**
	 * The Message type.
	 */
	private String messageType;

	/**
	 * The consumeThreadNums ,并发消费注意业务处理的幂等
	 */
	private int consumeThreadNums;

	/**
	 * 监听消息处理的BeanName
	 */
	private String messageListenerBeanName;

	/**
	 * 消费者上下文信息
	 */
	private Map<String, String> consumeContext;

}
