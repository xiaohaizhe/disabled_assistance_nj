package com.hd.home_disabled.utils.validation.emailvalidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName EmailValidationValidator
 * @Author pyt
 * @Date 2019/1/8 10:30
 */
public class EmailValidationValidator implements ConstraintValidator<EmailValidation, String> {
    private static  final Pattern PHONE_PATTERN = Pattern.compile(
            "^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$"
    );
    @Override
    public void initialize(EmailValidation constraintAnnotation) {

    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null || s.length() == 0){
            return true;
        }
        Matcher m = PHONE_PATTERN.matcher(s);
        return m.matches();
    }
}
