package com.palest.ink.secure.common.enums;

/**
 * 敏感字段类型的枚举
 *
 * @author jiangchenxi on 2017/6/21.
 */
public enum SensitiveFieldTypeEnum {

    /** 无需脱敏 */
    NONE,

    /** 通用类型，主要是字符串，也包括支持脱敏的实体对象或实体对象的集合 */
    GENERAL,

    /** json类型 */
    JSON,

}
