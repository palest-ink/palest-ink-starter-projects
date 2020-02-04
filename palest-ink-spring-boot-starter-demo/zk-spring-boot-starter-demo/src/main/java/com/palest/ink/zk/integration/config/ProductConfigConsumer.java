package com.palest.ink.zk.integration.config;

import com.palest.ink.zk.event.ZkEventData;
import com.palest.ink.zk.integration.listener.PathNodeDataConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * 读取产品配置
 *
 * @author WeiHui
 * @date 2019/1/21
 */
@Slf4j
@Configuration
public class ProductConfigConsumer implements PathNodeDataConsumer {

	@Override
	public void consume(ZkEventData zkEventData) {
		log.info("====>ZkEventType: {} \n {}", zkEventData.getEventType(), zkEventData.getData());
	}

}
