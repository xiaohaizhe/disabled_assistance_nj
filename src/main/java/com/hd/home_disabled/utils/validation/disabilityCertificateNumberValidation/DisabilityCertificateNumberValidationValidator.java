package com.hd.home_disabled.utils.validation.disabilityCertificateNumberValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName DisabilityCertificateNumberValidationValidator
 * @Description TODO
 * @Author pyt
 * @Date 2019/7/5 10:52
 * @Version
 */
public class DisabilityCertificateNumberValidationValidator implements ConstraintValidator<DisabilityCertificateNumberValidation, String> {
    private static  final Pattern reg = Pattern.compile(
            "(^\\d{22}$)|(^\\d{20}$)"
    );
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null || s.length() == 0){
            return true;
        }
        Matcher m = reg.matcher(s);
        return m.matches();
    }
}
