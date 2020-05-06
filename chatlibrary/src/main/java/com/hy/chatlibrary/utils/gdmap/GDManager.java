package com.hy.chatlibrary.utils.gdmap;

import android.content.Context;

import com.amap.api.navi.enums.NaviType;
import com.hrw.gdlibrary.GDHelper;

/**
 * @version 1.0.0
 * @author:hrw
 * @date:2020/03/03 15:11
 * @desc:
 */
public class GDManager {
    private static GDManager mGdManager;
    private GDHelper mGdHelper;

    private GDManager(Context context) {

        mGdHelper = new GDHelper.Builder()
//                .setApiKey("5d3dec80aaa9577dbb14fe2982875981")//家庭电脑
                .setOpenXFYunVoice(false)
                .setNaviType(NaviType.GPS)
                .build(context);
    }

    public static void init(Context context) {
        if (mGdManager == null) {
            mGdManager = new GDManager(context);
        }
    }

    public static GDManager getInstance() {
        return mGdManager;
    }

    public GDHelper getGdHelper() {
        return mGdHelper;
    }
}
