package com.hd.home_disabled.utils;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @ClassName JasyptUtil
 * @Description TODO
 * @Author pyt
 * @Date 2019/7/1 11:58
 * @Version
 */
@Component
public class JasyptUtil {
    @Value("${jasypt.encryptor.password}")
    private String password;

    @Value("${jasypt.encryptor.algorithm}")
    private String algorithm;

    private  StandardPBEStringEncryptor getEncryptor() {
        StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();
        EnvironmentPBEConfig config = new EnvironmentPBEConfig();
        config.setAlgorithm(algorithm);
        config.setPassword(password);
        standardPBEStringEncryptor.setConfig(config);
        return standardPBEStringEncryptor;
    }


    public String encrypt(String var) {
        StandardPBEStringEncryptor standardPBEStringEncryptor = getEncryptor();
        String result = standardPBEStringEncryptor.encrypt(var);
        return result;
    }

    public String decrypt(String var) {
        StandardPBEStringEncryptor standardPBEStringEncryptor = getEncryptor();
        String result = standardPBEStringEncryptor.decrypt(var);
        return result;
    }

}
