package com.hy.chatlibrary.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author:MtBaby
 * @date:2020/05/11 14:28
 * @desc:
 */
public class SPHelper {
    public static String IS_OPEN_GROUP_NAME = "IS_OPEN_GROUP_NAME";
    private static SPHelper mSPHelper;
    private SharedPreferences mPreferences;

    private SPHelper(Context context) {
        mPreferences = context.getSharedPreferences("HY_IM", Context.MODE_PRIVATE);
    }

    public static SPHelper getInstance(Context context) {
        if (mSPHelper == null) {
            synchronized (SPHelper.class) {
                if (mSPHelper == null) {
                    mSPHelper = new SPHelper(context);
                }
            }
        }
        return mSPHelper;
    }

    public void putBoolean(String key, boolean value) {
        mPreferences.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key) {
        return mPreferences.getBoolean(key, false);
    }

    public void putString(String key, String value) {
        mPreferences.edit().putString(key, value).apply();
    }

    public String getString(String key) {
        return mPreferences.getString(key, null);
    }
}
