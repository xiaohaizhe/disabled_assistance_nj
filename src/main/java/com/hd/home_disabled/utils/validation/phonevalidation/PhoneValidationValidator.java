package com.hd.home_disabled.utils.validation.phonevalidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @ClassName PhoneValidationValidator
 * @Author pyt
 * @Date 2019/1/8 9:59
 */
public class PhoneValidationValidator implements ConstraintValidator<PhoneValidation, String> {
    private static final String PHONE = "^"
            + "((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))" // 手机号前3位
            + "\\d{8}" // 手机号后8位
            + "$";

    private static final String TEL_PATTERN_1 = "^"
            + "\\d{3}" // 地区码
            + "[-]"     //
            + "\\d{8}" // 座机号码
            + "$" + "|"
            + "^"
            + "\\d{4}" // 地区码
            + "[-]"     //
            + "\\d{7}" // 座机号码
            + "$";

    private static final String TEL_PATTERN_2 = "^"
            + "\\d{8}" // 座机号码
            + "$";

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s.matches(PHONE) || s.matches(TEL_PATTERN_1) || s.matches(TEL_PATTERN_2);
    }
}
