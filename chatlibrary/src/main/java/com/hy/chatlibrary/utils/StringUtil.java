package com.hy.chatlibrary.utils;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author:MtBaby
 * @date:2020/04/19 11:09
 * @desc:
 */
public class StringUtil {
    public static String isEmpty(String label) {
        return isEmpty(label, "");
    }

    public static String isEmpty(String label, String defaultStr) {
        return TextUtils.isEmpty(label) ? defaultStr : label;
    }

    /**
     * 正则表达式匹配两个指定字符串中间的内容
     * @param soap
     * @return
     */
    public static List<String> getSubUtil(String soap, String rgex){
        //"@(.*?) "
        List<String> list = new ArrayList<String>();
        Pattern pattern = Pattern.compile(rgex);// 匹配的模式
        Matcher m = pattern.matcher(soap);
        while (m.find()) {
            int i = 1;
            list.add(m.group(i));
            i++;
        }
        return list;
    }
}
