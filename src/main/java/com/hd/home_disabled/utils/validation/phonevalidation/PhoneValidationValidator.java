package com.hd.home_disabled.utils.validation.phonevalidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName PhoneValidationValidator
 * @Author pyt
 * @Date 2019/1/8 9:59
 */
public class PhoneValidationValidator implements ConstraintValidator<PhoneValidation, String> {
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$"
    );

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null || s.length() == 0) {
            return false;
        }
        Matcher m = PHONE_PATTERN.matcher(s);
        return m.matches();
    }

    @Override
    public void initialize(PhoneValidation constraintAnnotation) {

    }
}
