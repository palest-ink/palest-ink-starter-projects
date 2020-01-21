package com.palest.ink.secure.fieldhandlers;

import java.lang.reflect.Field;


/**
 * 敏感字段处理器接口
 *
 * @author  jiangchenxi on 2017/6/21.
 */
public interface FieldHandler {

    /** 是否忽略 */
    boolean ignore(Field field);

    /** 获取字段值 */
    Object getValue(Field field, Object fieldValue);
}
