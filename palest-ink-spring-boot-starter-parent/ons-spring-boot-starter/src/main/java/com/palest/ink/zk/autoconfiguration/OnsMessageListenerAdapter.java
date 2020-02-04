package com.zhongan.fcp.pre.kepler.common.config.listener.ons;

import com.aliyun.openservices.ons.api.*;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Properties;

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
	 * 默认消费线程
	 */
	private static final int DEFAULT_CONSUME_THREAD_NUMS = 10;

	/**
	 * 默认产品key
	 */
	private static final String DEFAULT_PRODUCT_CODE_KEY = "productCode";

	/**
	 * 默认监听BeanName
	 */
	private static final String DEFAULT_MESSAGE_LISTENER_BEAN_NAME = "onsMessageListener";

	/**
	 * 广播模式
	 *
	 * @link {com.aliyun.openservices.ons.api.ExpressionType}
	 */
	private static final String DEFAULT_MESSAGE_TYPE = "*";

	/**
	 * ons配置
	 */
	@Getter
	@Setter
	private OnsConfigBean onsConfigBean;

	/**
	 * ons配置
	 */
	@Getter
	@Setter
	private transient Object delegate;

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

	@Override
	public void afterPropertiesSet() {
		checkConfig();
		start();
	}

	@Override
	public Action consume(Message message, ConsumeContext consumeContext) {
		return delegate instanceof MessageListener ? ((MessageListener) delegate).consume(message, consumeContext) : ((KeplerMessageListener) delegate).consume(message, onsConfigBean);
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
		Assert.isTrue(this.onsConfigBean != null, "onsConfigBean can not be null");
		Assert.isTrue(this.defaultListableBeanFactory != null, "defaultListableBeanFactory can not be null");

		Assert.isTrue(StringUtils.hasText(this.onsConfigBean.getConsumerId()), "ons Consumer properties 'GROUP_ID' can not be null");
		Assert.isTrue(StringUtils.hasText(this.onsConfigBean.getTopic()), "ons Consumer properties 'topic' can not be null");
		Assert.isTrue(StringUtils.hasText(this.onsConfigBean.getAccesskey()), "ons Consumer properties 'AccessKey' can not be null");
		Assert.isTrue(StringUtils.hasText(this.onsConfigBean.getSecretkey()), "ons Consumer properties 'SecretKey' can not be null");
		Assert.isTrue(StringUtils.hasText(this.onsConfigBean.getOnsAddr()) || StringUtils.hasText(this.onsConfigBean.getNamesrvAddr()), "ons Consumer properties 'ONSAddr' or 'NAMESRV_ADDR' can not be both null");

		if (this.onsConfigBean.getConsumeThreadNums() == 0) {
			this.onsConfigBean.setConsumeThreadNums(DEFAULT_CONSUME_THREAD_NUMS);
		}
		if (!StringUtils.hasText(this.onsConfigBean.getProductCodeKey())) {
			this.onsConfigBean.setProductCodeKey(DEFAULT_PRODUCT_CODE_KEY);
		}
		if (!StringUtils.hasText(this.onsConfigBean.getMessageType())) {
			this.onsConfigBean.setMessageType(DEFAULT_MESSAGE_TYPE);
		}
		if (!StringUtils.hasText(this.onsConfigBean.getMessageListenerBeanName())) {
			this.onsConfigBean.setMessageListenerBeanName(DEFAULT_MESSAGE_LISTENER_BEAN_NAME);
		}
		Object bean = defaultListableBeanFactory.getBean(onsConfigBean.getMessageListenerBeanName());
		setDelegate(bean);
	}

	/**
	 * 启动消费者
	 */
	private void start() {
		Properties properties = new Properties();
		properties.put(PropertyKeyConst.GROUP_ID, this.onsConfigBean.getConsumerId());
		properties.put(PropertyKeyConst.AccessKey, this.onsConfigBean.getAccesskey());
		properties.put(PropertyKeyConst.SecretKey, this.onsConfigBean.getSecretkey());
		String onsAddr = this.onsConfigBean.getOnsAddr();
		if (StringUtils.hasText(onsAddr)) {
			properties.put(PropertyKeyConst.ONSAddr, onsAddr);
		} else {
			properties.put(PropertyKeyConst.NAMESRV_ADDR, this.onsConfigBean.getNamesrvAddr());
		}
		properties.put(PropertyKeyConst.ConsumeThreadNums, this.onsConfigBean.getConsumeThreadNums());

		String topic = this.onsConfigBean.getTopic();
		String apiName = onsConfigBean.getApiName();

		this.consumer = ONSFactory.createConsumer(properties);
		this.consumer.subscribe(topic, this.onsConfigBean.getMessageType(), this);
		this.consumer.start();

		log.info("====>ons topic:{}, apiName:  {} start successful", topic, apiName);
	}

}
