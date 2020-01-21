package com.palest.ink.zk;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author WeiHui
 * @date 2019/1/21
 */
@Data
@AllArgsConstructor
public class ZkEventData {

	/**
	 * 全路径
	 */
	private final String path;

	/**
	 * 事件
	 */
	private final ZkEventType eventType;

	/**
	 * 数据
	 */
	private final String data;

}
