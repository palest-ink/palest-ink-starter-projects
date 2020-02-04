package com.palest.ink.zk.integration.config;

import com.palest.ink.zk.integration.listener.PathNodeDataConsumerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author WeiHui
 * @date 2019/1/22
 */
@Component
public class RouterConsumer extends PathNodeDataConsumerAdapter {

	private static final Logger log = LoggerFactory.getLogger(RouterConsumer.class);

	@Override
	protected void handleNodeAdded(String path, String nodeValue) {
		log.info("====>{}==>{}", path, nodeValue);
	}

	@Override
	protected void handleNodeUpdated(String path, String nodeValue) {
		log.info("====>{}==>{}", path, nodeValue);
	}

	@Override
	protected void handleNodeRemoved(String path, String nodeValue) {
		log.info("====>{}==>{}", path, nodeValue);
	}
}
