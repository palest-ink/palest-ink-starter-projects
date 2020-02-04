package com.palest.ink.zk.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * zk工具类
 *
 * @author weihui
 */
@Slf4j
public class ZookeeperContext {

	private static CuratorFramework client;

	@EventListener
	public static void init(ApplicationReadyEvent event) {
		ConfigurableApplicationContext applicationContext = event.getApplicationContext();
		Map<String, CuratorFramework> curatorFrameworkMap = applicationContext.getBeansOfType(CuratorFramework.class);
		Map.Entry<String, CuratorFramework> curatorFrameworkEntry = curatorFrameworkMap.entrySet().iterator().next();
		client = curatorFrameworkEntry.getValue();
	}

	public static CuratorFramework getClient() {
		return client;
	}

	public static void setClient(CuratorFramework client) {
		ZookeeperContext.client = client;
	}

	/**
	 * 新建或保存节点
	 *
	 * @param path
	 * @param data
	 * @return 返回path
	 * @throws Exception
	 */
	public static String createOrUpdateData(String path, String data) throws Exception {
		return getClient().create()
				// 如果节点存在则Curator将会使用给出的数据设置这个节点的值
				.orSetData()
				// 如果指定节点的父节点不存在，则Curator将会自动级联创建父节点
				.creatingParentContainersIfNeeded()
				.forPath(path, data.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * 创建path，如果path存在不报错，静默返回path名称
	 *
	 * @param path
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String createData(String path, String data) throws Exception {
		if (exist(path)) {
			return path;
		}
		return getClient().create()
				// 如果指定节点的父节点不存在，则Curator将会自动级联创建父节点
				.creatingParentContainersIfNeeded()
				.forPath(path, data.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * 路径是否存在
	 *
	 * @param path 路径
	 * @return true-存在 false-不存在
	 */
	public static boolean exist(String path) {
		try {
			return client.checkExists().forPath(path) != null;
		} catch (Exception e) {
			return false;
		}
	}

	public static String getData(String path) throws Exception {
		return new String(getClient().getData()
				.forPath(path), StandardCharsets.UTF_8);
	}

	public static void delete(String path) throws Exception {
		getClient().delete().deletingChildrenIfNeeded().forPath(path);
	}

	public static List<String> getChildren(String path) throws Exception {
		return getClient().getChildren().forPath(path);
	}

	/**
	 * 监听一个节点
	 *
	 * @param path
	 * @param onChange 节点修改后触发
	 * @return 返回path
	 * @throws Exception
	 */
	public static String listenPath(String path, Consumer<NodeCache> onChange) throws Exception {
		String ret = createOrUpdateData(path, "{}");
		final NodeCache cache = new NodeCache(client, path, false);
		cache.getListenable().addListener(new NodeCacheListener() {

			@Override
			public void nodeChanged() throws Exception {
				onChange.accept(cache);
			}
		});
		cache.start();
		return ret;
	}

	/**
	 * 获取子节点信息并监听子节点
	 *
	 * @param parentPath   父节点路径
	 * @param listConsumer 子节点数据
	 * @param listener     监听事件
	 * @throws Exception
	 */
	public static void getChildrenAndListen(String parentPath, Consumer<List<ChildData>> listConsumer, PathChildrenCacheListener listener) throws Exception {
		// 为子节点添加watcher
		// PathChildrenCache: 监听数据节点的增删改，可以设置触发的事件
		PathChildrenCache childrenCache = new PathChildrenCache(client, parentPath, true);

		/**
		 * StartMode: 初始化方式
		 * POST_INITIALIZED_EVENT：异步初始化，初始化之后会触发事件
		 * NORMAL：异步初始化
		 * BUILD_INITIAL_CACHE：同步初始化
		 */
		childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);

		// 列出子节点数据列表，需要使用BUILD_INITIAL_CACHE同步初始化模式才能获得，异步是获取不到的
		List<ChildData> childDataList = childrenCache.getCurrentData();
		listConsumer.accept(childDataList);
		log.info("监听子节点增删改，监听路径:{}", parentPath);
		// 监听根节点下面的子节点
		childrenCache.getListenable().addListener(listener);
	}

	/**
	 * 获取子节点信息
	 *
	 * @param parentPath   父节点路径
	 * @param listConsumer 子节点数据
	 * @throws Exception
	 */
	public static void getChildrenData(String parentPath, Consumer<List<ChildData>> listConsumer) throws Exception {
		// 为子节点添加watcher
		// PathChildrenCache: 监听数据节点的增删改，可以设置触发的事件
		PathChildrenCache childrenCache = new PathChildrenCache(client, parentPath, true);

		/**
		 * StartMode: 初始化方式
		 * POST_INITIALIZED_EVENT：异步初始化，初始化之后会触发事件
		 * NORMAL：异步初始化
		 * BUILD_INITIAL_CACHE：同步初始化
		 */
		childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);

		// 列出子节点数据列表，需要使用BUILD_INITIAL_CACHE同步初始化模式才能获得，异步是获取不到的
		List<ChildData> childDataList = childrenCache.getCurrentData();
		listConsumer.accept(childDataList);
		childrenCache.close();
	}

	/**
	 * 监听子节点的增删改
	 *
	 * @param parentPath 父节点路径
	 * @param listener
	 * @throws Exception
	 */
	public static void listenChildren(String parentPath, PathChildrenCacheListener listener) throws Exception {
		// 为子节点添加watcher
		// PathChildrenCache: 监听数据节点的增删改，可以设置触发的事件
		PathChildrenCache childrenCache = new PathChildrenCache(client, parentPath, true);

		/**
		 * StartMode: 初始化方式
		 * POST_INITIALIZED_EVENT：异步初始化，初始化之后会触发事件
		 * NORMAL：异步初始化
		 * BUILD_INITIAL_CACHE：同步初始化
		 */
		childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
		// 监听根节点下面的子节点
		childrenCache.getListenable().addListener(listener);
	}

}