package com.atguigu.core.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-16 19:08
 * @version: 1.0
 * @modified By:十一。
 */
public class MessageUtils {

    public static boolean isPhoneNum(String phoneNum) {

        String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
        if (phoneNum.length() != 11) {
            return false;
        } else {
            Pattern compile = Pattern.compile(regex);
            Matcher matcher = compile.matcher(phoneNum);
            boolean bool = matcher.matches();
            return bool;
        }
    }

}
