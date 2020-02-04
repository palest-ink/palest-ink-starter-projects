package com.palest.ink.zk.integration.config;

import com.alibaba.fastjson.JSONObject;
import com.palest.ink.zk.event.AfterUpdate;
import com.palest.ink.zk.event.ZkEventType;
import com.palest.ink.zk.integration.listener.AbstractPathNodeDataConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author WeiHui
 * @date 2019/1/21
 */
@Slf4j
@Component
public class ProductConfigConsumer2 extends AbstractPathNodeDataConsumer {

	@AfterUpdate(ZkEventType.ADDED)
	public void init(JSONObject jsonObject) {
		log.info("====>ADDED:{}", jsonObject);
	}

	@AfterUpdate(ZkEventType.UPDATED)
	public void update(JSONObject jsonObject) {
		log.info("====>UPDATED: {}", jsonObject);
	}

	@AfterUpdate(ZkEventType.REMOVED)
	public void delete(JSONObject jsonObject) {
		log.info("====>REMOVED{}", jsonObject);
	}

}
