package com.hd.home_disabled.utils.validation.disabilityCertificateNumberValidation;

import com.hd.home_disabled.utils.validation.idNumberValidation.IdNumberValidationValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @ClassName DisabilityCertificateNumberValidationValidator
 * @Description TODO
 * @Author pyt
 * @Date 2019/7/5 10:52
 * @Version
 */
public class DisabilityCertificateNumberValidationValidator implements ConstraintValidator<DisabilityCertificateNumberValidation, String> {
    //第一代残疾证验证
    private static final String REGEX_1 = "^[\u5b81]"
            +"[\u7384]"
            +"[\u5b57][\u7b2c]"
            +"\\d{5}"
            +"[\u53f7]"
            + "$";
    //第二代残疾证验证
    private static final String REGEX_2 = "^"
            + "\\d{18}" // 18位二代身份证
            + "[1-8]" // 残疾类别
            + "[1-4]" // 残疾等级
            + "$";

    private static Boolean checkIDNo(String s) {
        if (s.matches(REGEX_2)) {
            String s1 = s.substring(0, 18);
            return IdNumberValidationValidator.checkIDNo(s1);
        }else return s.matches(REGEX_1);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return checkIDNo(s);
    }
}
