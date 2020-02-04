package com.palest.ink.ons.properties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author August.Zhang
 * @version v1.0.0
 * @date 2020/1/22 17:34
 * @since JDK 1.8
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class OnsProperties {

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

}
