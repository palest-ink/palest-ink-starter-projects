package com.palest.ink.ons.listener;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.palest.ink.ons.serialize.Serializer;
import com.palest.ink.ons.serialize.StringSerializer;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * @author August.Zhang
 * @version v1.0.0
 * @date 2019/11/12 17:01
 * @since JDK 1.8
 */
public class OnsMessageListenerAdapter implements InitializingBean, DisposableBean, MessageListener {

	/**
	 * logger
	 */
	private static final Logger log = LoggerFactory.getLogger(OnsMessageListenerAdapter.class);

	/**
	 * Out-of-the-box value for the default listener method: "handleMessage".
	 */
	private static final String ORIGINAL_DEFAULT_LISTENER_METHOD = "handleMessage";

	/**
	 * 原来默认的序列化器
	 */
	private static final Serializer<String> ORIGINAL_DEFAULT_SERIALIZER = new StringSerializer();

	@Getter
	@Setter
	private String defaultListenerMethod = ORIGINAL_DEFAULT_LISTENER_METHOD;

	@Getter
	@Setter
	private Serializer<?> serializer = ORIGINAL_DEFAULT_SERIALIZER;

	/**
	 * ons配置
	 */
	@Getter
	@Setter
	private OnsConsumerConfig onsConsumerConfig;

	/**
	 * ons配置
	 */
	@Getter
	@Setter
	private transient Object delegate;

	/**
	 * 方法调用
	 */
	private volatile MethodInvoker invoker;

	/**
	 * 消息监听BeanName
	 */
	@Getter
	@Setter
	protected transient DefaultListableBeanFactory defaultListableBeanFactory;

	/**
	 * 消费者
	 */
	private transient Consumer consumer;

	/**
	 * 属性设置
	 */
	@Override
	public void afterPropertiesSet() {
		checkConfig();
		start();
	}

	/**
	 * 消费
	 *
	 * @param message        消息
	 * @param consumeContext 消费环境
	 * @return {@link Action}
	 */
	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public Action consume(Message message, ConsumeContext consumeContext) {
		String topic = message.getTopic();
		String msgId = message.getMsgID();
		log.debug("ons consume msg , msgId:{},topic: {},", msgId, topic);

		try {
			if (delegate != this && delegate instanceof MessageListener) {
				return ((MessageListener) delegate).consume(message, consumeContext);
			}
			if (delegate != this && delegate instanceof OnsMessageListener) {
				return ((OnsMessageListener) delegate).consume(extractMessage(message), onsConsumerConfig.getConsumeContext());
			}
			// Regular case: find a handler method reflectively.
			Object convertedMessage = extractMessage(message);
			Object[] listenerArguments = new Object[]{convertedMessage};
			return (Action) invokeListenerMethod(invoker.getMethodName(), listenerArguments);
		} catch (Throwable th) {
			handleListenerException(th);
		}
		return Action.ReconsumeLater;
	}

	/**
	 * Handle the given exception that arose during listener execution. The default implementation logs the exception at
	 * error level.
	 *
	 * @param ex the exception to handle
	 */
	protected void handleListenerException(Throwable ex) {
		log.error("Listener execution failed", ex);
	}

	/**
	 * Extract the message body from the given Redis message.
	 *
	 * @param message the Redis <code>Message</code>
	 * @return the content of the message, to be passed into the listener method as argument
	 */
	protected Object extractMessage(Message message) {
		if (serializer != null) {
			return serializer.deserialize(message.getBody());
		}
		return message.getBody();
	}

	/**
	 * Invoke the specified listener method.
	 *
	 * @param methodName the name of the listener method
	 * @param arguments  the message arguments to be passed in
	 */
	protected Object invokeListenerMethod(String methodName, Object[] arguments) {
		try {
			return invoker.invoke(arguments);
		} catch (Throwable ex) {
			throw new IllegalArgumentException("Failed to invoke target method '" + methodName
					+ "' with arguments " + ObjectUtils.nullSafeToString(arguments), ex);
		}
	}

	@Override
	public void destroy() {
		if (this.consumer != null) {
			this.consumer.shutdown();
		}
	}

	/**
	 * 检查配置
	 */
	private void checkConfig() {
		Assert.isTrue(this.onsConsumerConfig != null, "onsConfigBean can not be null");
		Assert.isTrue(this.defaultListableBeanFactory != null, "defaultListableBeanFactory can not be null");

		Assert.isTrue(StringUtils.hasText(this.onsConsumerConfig.getConsumerId()), "ons Consumer properties 'GROUP_ID' can not be null");
		Assert.isTrue(StringUtils.hasText(this.onsConsumerConfig.getTopic()), "ons Consumer properties 'topic' can not be null");
		Assert.isTrue(StringUtils.hasText(this.onsConsumerConfig.getAccesskey()), "ons Consumer properties 'AccessKey' can not be null");
		Assert.isTrue(StringUtils.hasText(this.onsConsumerConfig.getSecretkey()), "ons Consumer properties 'SecretKey' can not be null");
		Assert.isTrue(StringUtils.hasText(this.onsConsumerConfig.getOnsAddr()) || StringUtils.hasText(this.onsConsumerConfig.getNamesrvAddr()), "ons Consumer properties 'ONSAddr' or 'NAMESRV_ADDR' can not be both null");

		Object bean = defaultListableBeanFactory.getBean(onsConsumerConfig.getMessageListenerBeanName());
		setDelegate(bean);

		String methodName = getDefaultListenerMethod();
		if (!StringUtils.hasText(methodName)) {
			throw new IllegalArgumentException("No default listener method specified: "
					+ "Either specify a non-null value for the 'defaultListenerMethod' property or "
					+ "override the 'getListenerMethodName' method.");
		}

		invoker = new MethodInvoker(delegate, methodName);
	}

	/**
	 * 启动消费者
	 */
	private void start() {
		Properties properties = new Properties();
		properties.put(PropertyKeyConst.GROUP_ID, this.onsConsumerConfig.getConsumerId());
		properties.put(PropertyKeyConst.AccessKey, this.onsConsumerConfig.getAccesskey());
		properties.put(PropertyKeyConst.SecretKey, this.onsConsumerConfig.getSecretkey());
		properties.put(PropertyKeyConst.ConsumeThreadNums, this.onsConsumerConfig.getConsumeThreadNums());

		String onsAddr = this.onsConsumerConfig.getOnsAddr();
		String nameSrvAddr = this.onsConsumerConfig.getNamesrvAddr();
		String printAddr;
		if (StringUtils.hasText(onsAddr)) {
			printAddr = onsAddr;
			properties.put(PropertyKeyConst.ONSAddr, onsAddr);
		} else {
			printAddr = nameSrvAddr;
			properties.put(PropertyKeyConst.NAMESRV_ADDR, nameSrvAddr);
		}

		String topic = this.onsConsumerConfig.getTopic();

		this.consumer = ONSFactory.createConsumer(properties);
		this.consumer.subscribe(topic, this.onsConsumerConfig.getMessageType(), this);
		this.consumer.start();

		log.info("====>ons topic:{}, onsAddr:  {}  start successful", topic, printAddr);
	}

	private static class MethodInvoker {

		/**
		 * 被委托的类
		 */
		private final Object delegate;

		/**
		 * 委托的类的方法名
		 */
		private String methodName;

		/**
		 * 合适的方法集合，代表可以监听一次，执行多个方法的操作
		 */
		private Set<Method> methods;

		/**
		 * 方法调用程序
		 *
		 * @param delegate   委托
		 * @param methodName 方法名称
		 */
		MethodInvoker(Object delegate, final String methodName) {
			this.delegate = delegate;
			this.methodName = methodName;
			this.methods = new HashSet<>();

			boolean lenient = delegate instanceof MessageListener;
			final Class<?> c = delegate.getClass();

			ReflectionUtils.doWithMethods(c, method -> {
				ReflectionUtils.makeAccessible(method);
				methods.add(method);
			}, new MostSpecificMethodFilter(methodName, c));

			Assert.isTrue(lenient || !methods.isEmpty(), "Cannot find a suitable method named [" + c.getName() + "#"
					+ methodName + "] - is the method public and has the proper arguments?");
		}


		/**
		 * 调用
		 *
		 * @param arguments 参数
		 * @return {@link Object}* @throws InvocationTargetException 调用目标异常
		 * @throws IllegalAccessException 非法访问异常
		 */
		Object invoke(Object[] arguments) throws InvocationTargetException, IllegalAccessException {
			Object[] message = new Object[]{arguments[0]};
			for (Method m : methods) {
				Class<?>[] types = m.getParameterTypes();
				Object[] args = types.length == 2 //
						&& types[0].isInstance(arguments[0])  //
						&& types[1].isInstance(arguments[1]) ? arguments : message;
				if (!types[0].isInstance(args[0])) {
					continue;
				}
				return m.invoke(delegate, args);
			}
			return null;
		}

		/**
		 * Returns the current methodName.
		 *
		 * @return the methodName
		 */
		public String getMethodName() {
			return methodName;
		}

	}

	/**
	 * @since 1.4
	 */
	static final class MostSpecificMethodFilter implements ReflectionUtils.MethodFilter {

		private final String methodName;

		private final Class<?> c;

		MostSpecificMethodFilter(String methodName, Class<?> c) {
			this.methodName = methodName;
			this.c = c;
		}

		@Override
		public boolean matches(Method method) {
			if (Modifier.isPublic(method.getModifiers()) //
					&& methodName.equals(method.getName()) //
					&& method.equals(ClassUtils.getMostSpecificMethod(method, c))) {

				// check out the argument numbers
				Class<?>[] parameterTypes = method.getParameterTypes();

				return ((parameterTypes.length == 2 && ConsumeContext.class.equals(parameterTypes[1])) || parameterTypes.length == 1);
			}
			return false;
		}

	}

}
