package com.zhongan.fcp.pre.kepler.common.config.listener.ons;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.Message;

/**
 * @author August.Zhang
 * @version v1.0.0
 * @date 2019/12/19 15:29
 * @since JDK 1.8
 */
public interface KeplerMessageListener {

	Action consume(Message message, OnsConfigBean onsConfigBean);

}
