package com.palest.ink.secure.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 类Sensitive.java的实现描述：格式为jsond的字段，含敏感信息.
 * <p>
 * 对于json特定的敏感信息格式化，如果json中某个或者多个字段的值需要脱敏，使用该注解
 * </p>
 *
 * @author wanghy 2017年3月28日 下午3:50:21
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SensitiveJson {

	/**
	 * json脱敏格式化方法.
	 * <p>
	 * example:
	 * <pre>
	 * 对于形如{"bankCardNo":"60059029302981203","mobileNo":"18982938240","amount":10000}这样的字符串，如果需要对其中的bankCardNo和mobileNo进行脱敏，格式化字符串应如下
	 * {"bankCardNo":"CARD_NO","mobileNo":"PHONE_NO"}
	 * rule值请参照：{@link com.palest.ink.secure.common.enums.SensitiveRulesEnum}
	 * </pre>
	 * </p>
	 *
	 * @return 格式化后字符串
	 */
	String format() default "";


	/**
	 * 是否忽略
	 *
	 * @return 默认不忽略
	 */
	boolean ignore() default false;

}
