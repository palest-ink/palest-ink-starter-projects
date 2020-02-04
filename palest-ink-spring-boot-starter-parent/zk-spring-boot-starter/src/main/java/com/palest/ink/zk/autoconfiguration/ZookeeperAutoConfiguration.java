package com.palest.ink.zk.autoconfiguration;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.palest.ink.zk.properties.ZookeeperProperties;
import com.palest.ink.zk.utils.ZookeeperContext;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * zk自动配置
 *
 * @author August.zhang
 * @fileName: ZookeeperAutoConfiguration.java
 * @date: 2019/1/11 17:37
 * @version: v1.0.0
 * @since JDK 1.8
 */
@Configuration
@EnableConfigurationProperties(ZookeeperProperties.class)
public class ZookeeperAutoConfiguration {

	/**
	 * logger
	 */
	private final Logger logger = LoggerFactory.getLogger(ZookeeperAutoConfiguration.class);

	/**
	 * 线程工厂
	 */
	private ThreadFactory threadFactory = new ThreadFactoryBuilder()
			.setDaemon(true)
			.setNameFormat("PathNodeListener-%s")
			.build();

	/**
	 * zk的属性
	 */
	@Resource
	private ZookeeperProperties zookeeperProperties;

	/**
	 * zk框架
	 *
	 * @return {@link CuratorFramework}
	 */
	@Bean
	public CuratorFramework curatorFramework() {
		ZookeeperProperties zkConfig = zookeeperProperties;
		logger.debug("zookeeper registry center init, server lists is: {}.", zkConfig.getServerLists());

		CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
				.threadFactory(threadFactory)
				.connectString(zkConfig.getServerLists())
				.retryPolicy(new ExponentialBackoffRetry(
						zkConfig.getBaseSleepTimeMilliseconds(),
						zkConfig.getMaxRetries(),
						zkConfig.getMaxSleepTimeMilliseconds()))
				.namespace(zkConfig.getNamespace());
		if (0 != zkConfig.getSessionTimeoutMilliseconds()) {
			builder.sessionTimeoutMs(zkConfig.getSessionTimeoutMilliseconds());
		}
		if (0 != zkConfig.getConnectionTimeoutMilliseconds()) {
			builder.connectionTimeoutMs(zkConfig.getConnectionTimeoutMilliseconds());
		}
		if (!Strings.isNullOrEmpty(zkConfig.getDigest())) {
			builder.authorization("digest", zkConfig.getDigest().getBytes(Charsets.UTF_8))
					.aclProvider(new ACLProvider() {

						@Override
						public List<ACL> getDefaultAcl() {
							return ZooDefs.Ids.CREATOR_ALL_ACL;
						}

						@Override
						public List<ACL> getAclForPath(final String path) {
							return ZooDefs.Ids.CREATOR_ALL_ACL;
						}
					});
		}
		CuratorFramework curatorFramework = builder.build();
		curatorFramework.start();
		try {
			if (!curatorFramework.blockUntilConnected(zkConfig.getMaxSleepTimeMilliseconds() * zkConfig.getMaxRetries(), TimeUnit.MILLISECONDS)) {
				curatorFramework.close();
				throw new KeeperException.OperationTimeoutException();
			}
		} catch (Exception e) {
			logger.error("zk exception", e);
		}
		return curatorFramework;
	}

	/**
	 * zk上下文
	 *
	 * @return {@link ZookeeperContext}
	 */
	@Bean
	public ZookeeperContext zookeeperContext() {
		return new ZookeeperContext();
	}

}
