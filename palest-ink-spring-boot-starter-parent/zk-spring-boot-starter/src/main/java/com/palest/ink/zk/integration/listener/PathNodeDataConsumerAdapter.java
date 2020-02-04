package com.palest.ink.zk.integration.listener;

import com.palest.ink.zk.event.ZkEventData;
import com.palest.ink.zk.event.ZkEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 事件消费适配器
 *
 * @author WeiHui
 * @date 2019/1/22
 */
public class PathNodeDataConsumerAdapter implements PathNodeDataConsumer {

	/**
	 * 日志
	 */
	protected Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * 消费
	 *
	 * @param eventData 事件数据
	 */
	@Override
	public void consume(ZkEventData eventData) {
		final String data = eventData.getData();
		final ZkEventType eventType = eventData.getEventType();
		final String path = eventData.getPath();
		log.debug("监听到节点:{}的【{}】操作:{}", path, eventType, data);
		switch (eventType) {
			case ADDED:
				handleNodeAdded(path, data);
				break;
			case UPDATED:
				handleNodeUpdated(path, data);
				break;
			case REMOVED:
				handleNodeRemoved(path, data);
				break;
			default:
				log.error("异常事件{}", eventType);
				break;
		}
	}

	/**
	 * 监听新增
	 *
	 * @param path      路径
	 * @param nodeValue 值
	 */
	protected void handleNodeAdded(String path, String nodeValue) {
	}

	/**
	 * 监听更新
	 *
	 * @param path      路径
	 * @param nodeValue 值
	 */
	protected void handleNodeUpdated(String path, String nodeValue) {
	}

	/**
	 * 监听删除
	 *
	 * @param path      路径
	 * @param nodeValue 值
	 */
	protected void handleNodeRemoved(String path, String nodeValue) {
	}

}
