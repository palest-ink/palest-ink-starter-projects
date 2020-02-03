package com.palest.ink.secure.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.apache.commons.lang3.StringUtils;

/**
 * 类SensitiveRulesEnum1.java的实现描述：脱敏规则
 * 
 * @author wanghy 2017年5月23日 上午10:33:33
 */
@Getter
@AllArgsConstructor
public enum SensitiveRulesEnum {

    /** 全脱敏(如：******) */
    DESENSITIZED_FULLY("DESENSITIZED_FULLY", "任一字段全脱敏", "任意类型", RuleEnum.RULE_DESENSITIZED_FULLY),

    /** 不显示原数值 (如：N.A.) */
    DISSHOW_FIELD("DISSHOW_FIELD", "企业密钥|个人密钥|口令数据等", "不显示", RuleEnum.RULE_DISSHOW),

    /** 姓名 (如：何**) */
    NAME("NAME", "姓名", "个人信息", RuleEnum.RULE_NAME),

    /** 密码(如：******) */
    PASSWORD("PASSWORD", "密码", "个人信息", RuleEnum.RULE_PASSWORD),

    /** 银行卡号(如：6227****1104) */
    CARD_NO("CARD_NO", "银行卡号", "个人信息", RuleEnum.RULE_CARD_NO),

    /** 证件号(如：3****************7) */
    CERTI_NO("CERTI_NO", "证件号", "个人信息", RuleEnum.RULE_CERTI_NO),

    /** 手机号(如：133******78) */
    PHONE_NO("PHONE_NO", "手机号", "个人信息", RuleEnum.RULE_PHONE_NO),

    /** 邮箱 */
    EMAIL("EMAIL", "邮箱", "个人信息", RuleEnum.RULE_EMAIL),

    /** Hash值脱敏,规则： MD5 */
    HASH("HASH", "hash值", "hash值", RuleEnum.RULE_HASH),;

    /** 枚举名 */
    private String name;
    /** 脱敏字段 */
    private String dataField;
    /** 数据类型 */
    private String dataType;
    /** 脱敏规则 */
    private RuleEnum rule;

    /**
     * 通过枚举名获取枚举实例
     *
     * @param name
     * @return
     */
    public static SensitiveRulesEnum getSensitiveRule(String name){
        for(SensitiveRulesEnum rule :SensitiveRulesEnum.values()){
            if(rule.getName().equalsIgnoreCase(StringUtils.trim(name))){
                return rule;
            }
        }
        return null;
    }

    @Getter
    @AllArgsConstructor
    public enum RuleEnum {
        RULE_DESENSITIZED_FULLY(true, 6, 0, 0),
        RULE_DESENSITIZED_1_(true, 1, 1, 0),
        RULE_NAME(true, 2, 1, 0),
        RULE_PASSWORD(true, 6, 0, 0),
        RULE_CARD_NO(true, 4, 4, 4),
        RULE_CERTI_NO(true, 16, 1, 1),
        RULE_PHONE_NO(true, 6, 3, 2),
        RULE_EMAIL(true, 3, 2, 1),
        RULE_DISSHOW(false, 0, 0, 0),
        RULE_HASH(true, 0, 0, 0),;

        /** 是否显示 */
        private boolean isShow;
        /** 补充*号 长度 */
        private int paddingStar;
        /** 保留前*位（前指针） */
        private int beforeIndex;

        /** 保留后*位（后指针） */
        private int afterIndex;
    }

}
