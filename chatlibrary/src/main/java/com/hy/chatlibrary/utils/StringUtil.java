package com.hy.chatlibrary.utils;

import android.text.TextUtils;

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
}
