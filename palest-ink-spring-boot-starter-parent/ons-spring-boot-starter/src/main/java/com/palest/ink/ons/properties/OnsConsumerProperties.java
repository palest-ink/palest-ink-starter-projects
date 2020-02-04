package com.palest.ink.ons.properties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Map;

/**
 * Ons消费者配置
 *
 * @author August.Zhang
 * @version v1.0.0
 * @date 2019/11/12 16:56
 * @since JDK 1.8
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@ConfigurationProperties(prefix = "palest.ons")
public class OnsConsumerProperties {

	/**
	 * uid
	 */
	private static final long serialVersionUID = 1244790913556856856L;

	/**
	 * ons 配置
	 */
	@NestedConfigurationProperty
	private OnsProperties onsProperties;

	/**
	 * ons 监听配置
	 * key-beanName , value -> 消息的topic consumerId等配置
	 */
	private Map<String, OnsConsumerListenerProperties> onsConsumerListenerPropertiesMap;

	/**
	 * 监听属性配置
	 */
	@Data
	public static class OnsConsumerListenerProperties {

		/**
		 * 默认消费线程
		 */
		private static final int DEFAULT_CONSUME_THREAD_NUMS = 10;

		/**
		 * 广播模式
		 *
		 * @link {com.aliyun.openservices.ons.api.ExpressionType}
		 */
		private static final String DEFAULT_MESSAGE_TYPE = "*";

		/**
		 * 默认序列化方式
		 */
		private static final String DEFAULT_SERIALIZER_BEAN_NAME = "stringSerializer";

		/**
		 * The Topic.
		 */
		private String topic;

		/**
		 * The Consumer id.
		 */
		private String consumerId;

		/**
		 * The Message type.
		 */
		private String messageType = DEFAULT_MESSAGE_TYPE;

		/**
		 * The consumeThreadNums ,并发消费注意业务处理的幂等
		 */
		private int consumeThreadNums = DEFAULT_CONSUME_THREAD_NUMS;

		/**
		 * 序列化BeanName
		 */
		private String serializerBeanName = DEFAULT_MESSAGE_TYPE;

		/**
		 * 是否重试, 自定义队列重试 还是依靠 ons自己的重试呢
		 */
		private boolean retry;

		/**
		 * 重试次数
		 */
		private int retryTimes;

		/**
		 * 消费者额外参数 , 比如： 需要取到 productTag = "productCode" ，根据productCode不同做啥事
		 */
		private Map<String, String> consumeContext;

	}

}
