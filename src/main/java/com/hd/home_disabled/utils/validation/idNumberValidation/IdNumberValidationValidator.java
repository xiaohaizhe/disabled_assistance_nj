package com.hd.home_disabled.utils.validation.idNumberValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @ClassName IdNumberValidationValidator
 * @Description 身份证号校验器
 * @Author pyt
 * @Date 2019/7/5 10:25
 * @Version
 */
public class IdNumberValidationValidator implements ConstraintValidator<IdNumberValidation, String> {
    //18位二代身份证号码的正则表达式
    private static final String REGEX_ID_NO_18 = "^"
            + "\\d{6}" // 6位地区码
            + "(18|19|([23]\\d))\\d{2}" // 年YYYY
            + "((0[1-9])|(10|11|12))" // 月MM
            + "(([0-2][1-9])|10|20|30|31)" // 日DD
            + "\\d{3}" // 3位顺序码
            + "[0-9Xx]" // 校验码
            + "$";

    /**
     * 校验身份证号码
     * @param IDNo18 身份证号码
     * @return 校验结果
     * @throws IllegalArgumentException 如果身份证号码为空或长度不为18位或不满足身份证号码组成规则
     *                                  <i>6位地址码+
     *                                  出生年月日YYYYMMDD+3位顺序码
     *                                  +0~9或X(x)校验码</i>
     */
    public static boolean checkIDNo(String IDNo18) {
        // 校验身份证号码的长度 匹配身份证号码的正则表达式
        if (checkStrLength(IDNo18, 18) && regexMatch(IDNo18, REGEX_ID_NO_18)) {
            // 校验身份证号码的验证码
            return validateCheckNumber(IDNo18);
        }
        return false;
    }

    /**
     * 检查输入字符串inputString是否符合长度len
     * @param inputString 输入字符串
     * @param len 长度
     * @return  是否符合
     */
    private static boolean checkStrLength(String inputString, int len) {
        return inputString.length() == len;
    }

    /**
     * 匹配正则表达式
     *
     * @param inputString 字符串
     * @param regex       正则表达式
     * @return true - 校验通过<br>
     * false - 校验不通过
     */
    private static boolean regexMatch(String inputString, String regex) {
        return inputString.matches(regex);
    }

    /**
     * 校验码校验:适用于18位的二代身份证号码
     * @param IDNo18 身份证号码
     * @return 校验结果
     */
    private static boolean validateCheckNumber(String IDNo18) {
        // 加权因子
        int[] W = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        char[] IDNoArray = IDNo18.toCharArray();
        int sum = 0;
        for (int i = 0; i < W.length; i++) {
            sum += Integer.parseInt(String.valueOf(IDNoArray[i])) * W[i];
        }
        // 校验位是X，则表示10
        if (IDNoArray[17] == 'X' || IDNoArray[17] == 'x') {
            sum += 10;
        } else {
            sum += Integer.parseInt(String.valueOf(IDNoArray[17]));
        }
        // 如果除11模1，则校验通过
        return sum % 11 == 1;
    }
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return checkIDNo(s);
    }
}
