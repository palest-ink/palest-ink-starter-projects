package com.palest.ink.secure.fieldhandlers;

import com.alibaba.fastjson.JSON;
import com.palest.ink.secure.common.annotations.Sensitive;
import com.palest.ink.secure.common.constants.DataSecureConstants;
import com.palest.ink.secure.utils.SensitiveProcessUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通用字段脱敏处理器
 *
 * @author jiangchenxi on 2017/6/21.
 */
public class GeneralFieldHandler implements FieldHandler {

    @Override
    public boolean ignore(Field field) {
        Sensitive sensitive = field.getAnnotation(Sensitive.class);
        if (sensitive == null) {
            return false;
        }
        if (sensitive.ignore()) {
            return true;
        }
        return false;
    }

    @Override
    public Object getValue(Field field, Object fieldValue) {
        Sensitive sensitive = field.getAnnotation(Sensitive.class);
        if (sensitive == null || fieldValue == null) {
            return fieldValue;
        }

        if (fieldValue instanceof List) {
            String jsonStr = StringUtils.EMPTY;
            for (Object object : (List<?>) fieldValue) {
                if (object instanceof String) {
                    jsonStr = JSON.toJSONString(fieldValue);
                    break;
                }
            }
            if (!StringUtils.equals(jsonStr, StringUtils.EMPTY)) {
                Matcher matcher = Pattern.compile(DataSecureConstants.REGEX_LIST).matcher(jsonStr);
                while (matcher.find()) {
                    jsonStr = StringUtils.replace(jsonStr, matcher.group(1),
                            SensitiveProcessUtils.shield(sensitive.format(), matcher.group(1)));
                }
                return jsonStr;
            }
        }

        String value = String.valueOf(fieldValue);
        return SensitiveProcessUtils.shield(sensitive.format(), value);
    }
}
