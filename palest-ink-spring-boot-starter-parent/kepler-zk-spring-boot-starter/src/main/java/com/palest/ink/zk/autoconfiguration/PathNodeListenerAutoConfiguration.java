package com.palest.ink.zk.autoconfiguration;

import com.palest.ink.zk.integration.listener.PathNodeDataConsumer;
import com.palest.ink.zk.integration.listener.ZkPathTreeCacheListenerListener;
import com.palest.ink.zk.properties.ZookeeperProperties;
import com.palest.ink.zk.utils.Try;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 根据是否配置监听,从而判断是否启动消费事件线程
 *
 * @author WeiHui
 * @date 2019/1/21
 */
@Slf4j
@Configuration
@AllArgsConstructor
public class PathNodeListenerAutoConfiguration {

	private final ZookeeperProperties zookeeperProperties;

	private final CuratorFramework curatorFramework;

	@Bean
	public Executor zkAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(zookeeperProperties.getCorePoolSize());
		executor.setMaxPoolSize(zookeeperProperties.getMaxPoolSize());
		executor.setQueueCapacity(zookeeperProperties.getQueueCapacity());
		ThreadPoolExecutor.CallerRunsPolicy callerRunsPolicy = new ThreadPoolExecutor.CallerRunsPolicy();
		executor.setRejectedExecutionHandler(callerRunsPolicy);
		executor.setThreadNamePrefix("zkAsyncExecutor-");
		executor.initialize();
		return executor;
	}

	/**
	 * 通过配置文件批量注册bean
	 *
	 * @param applicationContext 自动注入
	 */
	@Bean
	public Optional<Void> pathNodeDataConsumer(ApplicationContext applicationContext, Executor zkAsyncExecutor) {
		Map<String, ZookeeperProperties.PathNodeListenerProperties> pathNodeListener = zookeeperProperties.getPathNodeListener();
		if (!CollectionUtils.isEmpty(pathNodeListener)) {
			pathNodeListener.forEach((k, v) -> {
				try {
					boolean enable = v.isEnable();
					String pathNode = v.getPathNode();
					if (enable && StringUtils.isNotBlank(pathNode)) {
						PathNodeDataConsumer pathNodeDataConsumer = applicationContext.getBean(k, PathNodeDataConsumer.class);
						Arrays.stream(pathNode.split(",", -1)).forEach(Try.ofConsumer(node -> {
							String nodePath = node.startsWith("/") ? node : "/" + node;
							//配置节点的监听类
							TreeCache treeCache = new TreeCache(curatorFramework, nodePath);
							treeCache.getListenable().addListener(new ZkPathTreeCacheListenerListener(pathNodeDataConsumer), zkAsyncExecutor);
							treeCache.start();
							log.debug("zk PathNodeDataConsumer Bean :{} listen the path:{}", pathNodeDataConsumer, node);
						}));
					}
				} catch (Exception e) {
					log.error("绑定zk节点和监听bean出错,beanName:{}", k, e);
				}
			});
		}
		return Optional.empty();
	}

}
