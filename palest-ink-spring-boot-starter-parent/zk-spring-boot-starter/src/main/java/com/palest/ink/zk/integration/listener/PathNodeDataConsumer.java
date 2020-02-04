package com.palest.ink.zk.integration.listener;


import com.palest.ink.zk.event.ZkEventData;

/**
 * 节点数据消费类
 *
 * @author WeiHui
 * @date 2019/1/21
 */
@FunctionalInterface
public interface PathNodeDataConsumer {

	/**
	 * 事件消费
	 *
	 * @param eventData 事件数据
	 */
	void consume(ZkEventData eventData);

}
