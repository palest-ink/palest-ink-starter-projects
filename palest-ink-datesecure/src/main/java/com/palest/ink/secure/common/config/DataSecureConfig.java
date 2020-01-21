/*
 * Copyright 2017 Zhongan.com All right reserved. This software is the
 * confidential and proprietary information of Zhongan.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Zhongan.com.
 */
package com.palest.ink.secure.common.config;

import com.palest.ink.secure.common.constants.DataSecureConstants;
import com.palest.ink.secure.common.enums.SensitiveRulesEnum;
import com.palest.ink.secure.utils.SecretKeyUtil;
import com.palest.ink.secure.utils.SensitiveProcessUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;

/**
 * 类SecureKeyConfig.java的实现描述：
 * 
 * @author huanglifang Jun 8, 2017 3:28:15 PM
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataSecureConfig {
    private String secretkey;
    private String secswitch = DataSecureConstants.IS_ENCRYPT_OPEN;//默认开

    @PostConstruct
    public void init() throws Exception {
        SecretKeyUtil.setSecretKey(secretkey);
        if (!DataSecureConstants.IS_ENCRYPT_OPEN.equals(secswitch)) {
            EncryptSwitchConfig.setEncryptFlag(false);
        }
        log.info("secretkey={},secswitch = {}", SensitiveProcessUtils.shield(SensitiveRulesEnum.NAME, secretkey),
                EncryptSwitchConfig.getEncryptFlag());
    }
}
