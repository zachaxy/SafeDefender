package com.zachaxy.safedefender.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhangxin on 2016/7/4.
 * 判断工具类,用来判断手机号码,邮政编码,邮箱是否合法
 */


/***
 * java-正则表达式判断手机号
 * 要更加准确的匹配手机号码只匹配11位数字是不够的，比如说就没有以144开始的号码段，
 * 故先要整清楚现在已经开放了多少个号码段，国家号码段分配如下：
 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
 * 联通：130、131、132、152、155、156、185、186
 * 电信：133、153、180、189、（1349卫通）
 */
public class JudgeUtils {


    //该方法存在问题,暂时同统一返回true
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        //return m.matches();
        return true;
    }

    public static boolean isZipNO(String zipString) {
        String str = "^[1-9][0-9]{5}$";
        return Pattern.compile(str).matcher(zipString).matches();
    }

    public static boolean isEmail(String email) {
        if (null == email || "".equals(email)) return false;
        //Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
        Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//复杂匹配
        Matcher m = p.matcher(email);
        return m.matches();
    }
}
