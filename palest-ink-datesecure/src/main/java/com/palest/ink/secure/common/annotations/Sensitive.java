package com.palest.ink.secure.common.annotations;


import com.palest.ink.secure.common.enums.SensitiveRulesEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 类Sensitive.java的实现描述：敏感字段
 * 
 * @author wanghy 2017年3月28日 下午3:50:21
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Sensitive {

    /**
     * 敏感信息格式化
     * <li>默认为全部屏蔽
     * 
     * @return
     */
    SensitiveRulesEnum format() default SensitiveRulesEnum.DESENSITIZED_FULLY;

    /**
     * 字段忽略
     * <li>默认不忽略
     * 
     * @return
     */
    boolean ignore() default false;
}
