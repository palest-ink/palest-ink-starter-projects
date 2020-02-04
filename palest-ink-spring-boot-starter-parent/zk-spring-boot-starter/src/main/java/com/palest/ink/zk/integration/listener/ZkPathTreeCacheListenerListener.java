package com.palest.ink.zk.integration.listener;

import com.palest.ink.zk.event.ZkEventData;
import com.palest.ink.zk.event.ZkEventType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;

import java.nio.charset.StandardCharsets;

/**
 * 节点监听
 *
 * @fileName: ZkPathTreeCacheListenerListener.java
 * @author: WeiHui
 * @date: 2019/1/18 17:49
 * @version: v1.0.0
 * @since JDK 1.8
 */
@Slf4j
public class ZkPathTreeCacheListenerListener implements TreeCacheListener {

	/**
	 * 路径节点数据消费者
	 */
	private PathNodeDataConsumer pathNodeDataConsumer;

	/**
	 * Zk路径树缓存监听器监听器
	 *
	 * @param pathNodeDataConsumer 路径节点数据消费者
	 */
	public ZkPathTreeCacheListenerListener(PathNodeDataConsumer pathNodeDataConsumer) {
		this.pathNodeDataConsumer = pathNodeDataConsumer;
	}

	/**
	 * 对zk事件的封装，只要我们关注的事件
	 *
	 * @param curatorFramework curator
	 * @param treeCacheEvent   节点事件
	 * @throws Exception ex
	 */
	@Override
	public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
		TreeCacheEvent.Type type = treeCacheEvent.getType();
		ChildData childData = treeCacheEvent.getData();
		//过滤数据为空的事件
		if (childData == null || childData.getData() == null) {
			log.trace("event received, type: {} , data was  null , discard this", type);
			return;
		}
		String data = new String(childData.getData(), StandardCharsets.UTF_8);
		if (StringUtils.isBlank(data)) {
			log.debug("event received, type: {} , data was  blank , discard this", type);
			return;
		}
		// 计算path
		String path = childData.getPath();
		// 计算-类型
		ZkEventType eType;
		if (TreeCacheEvent.Type.NODE_ADDED == type) {
			eType = ZkEventType.ADDED;
		} else if (TreeCacheEvent.Type.NODE_UPDATED == type) {
			eType = ZkEventType.UPDATED;
		} else if (TreeCacheEvent.Type.NODE_REMOVED == type) {
			eType = ZkEventType.REMOVED;
		} else {
			log.error("unknown message type: " + type);
			return;
		}
		//构造ZkEvent
		ZkEventData zkEventData = new ZkEventData(path, eType, data);
		//多线程消费,加快应用启动速度,实现类需要注意有线程安全问题
		pathNodeDataConsumer.consume(zkEventData);
	}

}
