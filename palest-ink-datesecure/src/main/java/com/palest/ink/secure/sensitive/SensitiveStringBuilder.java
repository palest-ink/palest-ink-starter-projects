package com.palest.ink.secure.sensitive;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.palest.ink.secure.common.annotations.Sensitive;
import com.palest.ink.secure.common.annotations.SensitiveJson;
import com.palest.ink.secure.common.constants.DataSecureConstants;
import com.palest.ink.secure.common.enums.SensitiveFieldTypeEnum;
import com.palest.ink.secure.fieldhandlers.FieldHandler;
import com.palest.ink.secure.fieldhandlers.FieldHandlerFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * 类SensitiveStringBuilder.java的实现描述：转换敏感字符串构建器
 * 
 * @author wanghy 2017年3月28日 下午5:53:23
 */
@Slf4j
public class SensitiveStringBuilder {

    /**
     * 获取当前字段的注解类型
     * 
     * @param field
     * @return
     */
    private static SensitiveFieldTypeEnum getFiledType(Field field) {
        if (field.isAnnotationPresent(Sensitive.class)) {
            return SensitiveFieldTypeEnum.GENERAL;
        }
        if (field.isAnnotationPresent(SensitiveJson.class)) {
            return SensitiveFieldTypeEnum.JSON;
        }
        return SensitiveFieldTypeEnum.NONE;
    }

    /**
     * @param object
     * @return
     */
    public static String reflectionToString(Object object) {
        try {
            ToStringBuilder toStringBuilder = (new ReflectionToStringBuilder(object, JToStringStyle.JSON_STYLE) {
                /**
                 * @see org.apache.commons.lang3.builder.ReflectionToStringBuilder#accept(java.lang.reflect.Field)
                 */
                @Override
                protected boolean accept(Field field) {
                    try {
                        Object fieldValue = super.getValue(field);
                        if (fieldValue == null)
                            return false;
                        if ((fieldValue instanceof String) && StringUtils.isBlank(String.valueOf(fieldValue)))
                            return false;
                        SensitiveFieldTypeEnum fieldType = getFiledType(field);
                        FieldHandler fieldHandler = FieldHandlerFactory.getFieldHandler(fieldType);
                        if (fieldHandler == null) {
                            return super.accept(field);
                        }
                        if (fieldHandler.ignore(field)) {
                            return false;
                        } else {
                            return super.accept(field);
                        }
                    } catch (Exception e) {
                        log.info("日志脱敏SensitiveStringBuilder#accept异常", e);
                    }
                    return false;
                }

                /**
                 * @see org.apache.commons.lang3.builder.ReflectionToStringBuilder#getValue(java.lang.reflect.Field)
                 */
                @Override
                protected Object getValue(Field field) {
                    Object fieldValue = null;
                    try {
                        fieldValue = super.getValue(field);
                        SensitiveFieldTypeEnum fieldType = getFiledType(field);
                        FieldHandler fieldHandler = FieldHandlerFactory.getFieldHandler(fieldType);
                        if (fieldHandler == null) {
                            if (fieldValue instanceof String || field.getType().isEnum()) {
                                return fieldValue;
                            } else if (fieldValue instanceof Date) {//日期做特殊格式化处理
                                return DataSecureConstants.DATE_FORMAT.get().format(fieldValue);
                            } else {
                                try {
                                    if (fieldValue.toString().startsWith(DataSecureConstants.LEFT_BRACE) || fieldValue
                                            .toString().startsWith(String.valueOf(DataSecureConstants.LEFT_BRACKET))) {
                                        return fieldValue.toString();
                                    } else {
                                        return JSON.toJSONString(fieldValue, SerializerFeature.WriteDateUseDateFormat);
                                    }
                                } catch (Exception e) {
                                    log.info("日志脱敏JSON.toJSONString异常", e);
                                    return fieldValue;
                                }
                            }
                        }
                        return fieldHandler.getValue(field, fieldValue);
                    } catch (Exception e) {
                        log.info("日志脱敏SensitiveStringBuilder#getValue异常", e);
                    }
                    return fieldValue;
                }
            });
            return toStringBuilder.toString();
        } catch (Exception e) {
            log.info("日志脱敏异常", e);
            return StringUtils.EMPTY;
        }
    }
}
