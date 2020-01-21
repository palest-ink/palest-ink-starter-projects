package com.palest.ink.secure.fieldhandlers;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.palest.ink.secure.common.annotations.SensitiveJson;
import com.palest.ink.secure.common.enums.SensitiveRulesEnum;
import com.palest.ink.secure.utils.SensitiveProcessUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * json字段脱敏处理器
 *
 * @author jiangchenxi on 2017/6/21.
 */
public class JsonFieldHandler implements FieldHandler {

	@Override
	public boolean ignore(Field field) {
		SensitiveJson sensitiveJson = field.getAnnotation(SensitiveJson.class);
		if (sensitiveJson == null) {
			return false;
		}
		if (sensitiveJson.ignore()) {
			return true;
		}
		return false;
	}

	@Override
	public Object getValue(Field field, Object fieldValue) {
		SensitiveJson sensitiveJson = field.getAnnotation(SensitiveJson.class);
		if (sensitiveJson == null || fieldValue == null) {
			return fieldValue;
		}

		String formatPattern = sensitiveJson.format();
		JSONObject formatPatternJson = null;
		formatPatternJson = JSONObject.parseObject(formatPattern);
		if (MapUtils.isEmpty(formatPatternJson)) {
			return fieldValue;
		}

		String jsonVal = StringUtils.EMPTY;
		if (fieldValue instanceof String) {
			jsonVal = String.valueOf(fieldValue);
		} else {
			jsonVal = JSON.toJSONString(fieldValue, SerializerFeature.WriteDateUseDateFormat);
		}

		Map<String, SensitiveRulesEnum> fieldsRuleMap = new HashMap<String, SensitiveRulesEnum>();
		for (Map.Entry<String, Object> entry : formatPatternJson.entrySet()) {
			String key = entry.getKey();
			String singleFormatPattern = String.valueOf(formatPatternJson.get(key));
			SensitiveRulesEnum sensitiveRulesEnum = SensitiveRulesEnum.getSensitiveRule(singleFormatPattern);
			if (sensitiveRulesEnum == null) {
				continue;
			}
			fieldsRuleMap.put(key, sensitiveRulesEnum);
		}
		return SensitiveProcessUtils.jsonShield(jsonVal, fieldsRuleMap);
	}

}
