package com.palest.ink.secure.common.config;

/**
 * 类EncryptSwitchConfig.java的实现描述：数据库加密开关
 * 
 * @author huanglifang Jun 2, 2017 11:37:05 AM
 */
public class EncryptSwitchConfig {
    /**
     * 上线临时方案，增加一个字段做判断，控制是否进行加解密操作 正式上线后，都做加解密操作，标志可去掉 默认是打开的
     */
    private static boolean encryptFlag = true;

    /**
     * 通过hsf http调用应用系统开关，来进行直接控制
     * 
     * @param bflag
     */
    public static void setEncryptFlag(boolean bflag) {
        encryptFlag = bflag;
    }

    /**
     * 得到目前是否加密标志
     * 
     * @return
     */
    public static boolean getEncryptFlag() {
        return encryptFlag;
    }
}
