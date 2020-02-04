package com.palest.ink.zk.properties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * 动物园管理员的属性
 *
 * @author WeiHui-Z
 * @version v1.0.0
 * @date 2019/11/4 12:00
 * @since JDK 1.8
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@ConfigurationProperties(prefix = "palest.zk")
public class ZookeeperProperties {

	/**
	 * 连接Zookeeper服务器的列表. 包括IP地址和端口号. 多个地址用逗号分隔. 如: host1:2181,host2:2181
	 */
	private String serverLists = "127.0.0.1:2181";

	/**
	 * 命名空间.
	 */
	private String namespace = "distributed_lock";

	/**
	 * 等待重试的间隔时间的初始值. 单位毫秒.
	 */
	private int baseSleepTimeMilliseconds = 1000;

	/**
	 * 等待重试的间隔时间的最大值. 单位毫秒.
	 */
	private int maxSleepTimeMilliseconds = 3000;

	/**
	 * 最大重试次数.
	 */
	private int maxRetries = 3;

	/**
	 * 会话超时时间. 单位毫秒.
	 */
	private int sessionTimeoutMilliseconds;

	/**
	 * 连接超时时间. 单位毫秒.
	 */
	private int connectionTimeoutMilliseconds;

	/**
	 * 连接Zookeeper的权限令牌. 缺省为不需要权限验证.
	 */
	private String digest;

	/**
	 * 线程池核心线程数
	 */
	private int corePoolSize = 5;

	/**
	 * 线程池最大线程数
	 */
	private int maxPoolSize = 10;

	/**
	 * 线程池阻塞队列大小
	 */
	private int queueCapacity = 300;

	/**
	 * 监听配置 ,k-consumerBeanName v-监听节点
	 */
	private Map<String, PathNodeListenerProperties> pathNodeListener;

	/**
	 * 监听属性配置
	 */
	@Data
	public static class PathNodeListenerProperties {

		/**
		 * 监听节点
		 */
		private String pathNode;

		/**
		 * 是否启用, 默认启用，可以排除监听 kepler-insurance 的共用监听配置
		 */
		private boolean enable = true;

	}

}

