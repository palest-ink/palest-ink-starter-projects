package com.palest.ink.core.spi;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * @author August.Zhang
 * @version v1.0.0
 * @date 2020/1/20 16:36
 * @since JDK 1.8
 */
public abstract class AbstractSingletonFactoryWithPostProcessor<K, V> extends AbstractSingletonFactory<K, V> implements ApplicationContextAware, EnvironmentAware, PostProcessor {

	protected ApplicationContext applicationContext;

	protected DefaultListableBeanFactory defaultListableBeanFactory;

	protected Environment environment;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
		ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
		this.defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
		postProcessor();
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	/**
	 * 后置处理
	 */
	@Override
	public void postProcessor() {

	}

}
