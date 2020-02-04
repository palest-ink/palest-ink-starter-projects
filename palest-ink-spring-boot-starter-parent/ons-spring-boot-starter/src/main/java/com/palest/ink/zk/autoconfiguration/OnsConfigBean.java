package com.zhongan.fcp.pre.kepler.common.config.listener.ons;

import lombok.Data;

import java.io.Serializable;

/**
 * @author August.Zhang
 * @version v1.0.0
 * @date 2019/11/12 16:56
 * @since JDK 1.8
 */
@Data
public class OnsConfigBean implements Serializable {

	/**
	 * uid
	 */
	private static final long serialVersionUID = 1244790913556856856L;

	/**
	 * The Topic.
	 */
	private String topic;

	/**
	 * The Consumer id.
	 */
	private String consumerId;

	/**
	 * The Accesskey.
	 */
	private String accesskey;

	/**
	 * The Secretkey.
	 */
	private String secretkey;

	/**
	 * The Onsaddr.
	 */
	private String onsAddr;

	/**
	 * The namesrvAddr.
	 */
	private String namesrvAddr;

	/**
	 * The Message type.
	 */
	private String messageType;

	/**
	 * The consumeThreadNums ,并发消费注意业务处理的幂等
	 */
	private int consumeThreadNums;

	/**
	 * 定义转发时指定的apiName,各个产品根据apiName，找到要转发的routerName
	 */
	private String apiName;

	/**
	 * 所属产品的 productCodeKey，收单ons消息不返回productCode，返回 bizSys
	 */
	private String productCodeKey;

	/**
	 * 监听消息处理的BeanName
	 */
	private String messageListenerBeanName;

}
