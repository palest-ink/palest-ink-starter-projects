package com.palest.ink.zk;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author WeiHui
 * @date 2019/1/21
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterUpdate {

	/**
	 * zk事件
	 *
	 * @return 事件类型
	 */
	ZkEventType[] value();

}
