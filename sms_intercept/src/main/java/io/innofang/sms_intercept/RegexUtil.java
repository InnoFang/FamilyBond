package io.innofang.sms_intercept;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: Inno Fang
 * Time: 2017/10/3 14:07
 * Description:
 */


public class RegexUtil {

//    private static final String URL = "((http|ftp|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?";
    private static final String URL = "(http://|ftp://|https://|www){0,1}[^\u4e00-\u9fa5\\s]*?\\.(com|net|cn|me|tw|fr)[^\u4e00-\u9fa5\\s]*";

    public static boolean isSuspiciousSMS(String sms) {
        Pattern pattern = Pattern.compile(URL);

        Matcher matcher = pattern.matcher(sms);
        if (matcher.find()) {
            return true;
        }
        return false;
    }

}
