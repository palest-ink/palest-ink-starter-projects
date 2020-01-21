package com.palest.ink.secure.fieldhandlers;


import com.palest.ink.secure.common.enums.SensitiveFieldTypeEnum;

;

/**
 * 脱敏字段处理器工厂类
 *
 * @author jiangchenxi on 2017/6/21.
 */
public class FieldHandlerFactory {

    /** 通用处理器 */
    private static final FieldHandler GENERAL_FIELD_HANDLER = new GeneralFieldHandler();

    /** json处理器 */
    private static final FieldHandler JSON_FIELD_HANDLER = new JsonFieldHandler();

    /**
     * 获取字段处理器工厂方法
     * @param fieldTypeEnum
     * @return
     */
    public static FieldHandler getFieldHandler(SensitiveFieldTypeEnum fieldTypeEnum){

        switch (fieldTypeEnum){
            case GENERAL:
                return GENERAL_FIELD_HANDLER;
            case JSON:
                return JSON_FIELD_HANDLER;
            case NONE:
                return null;
            default:
                return null;
        }

    }
}
