package com.hy.chatlibrary.utils;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * @author:MtBaby
 * @date:2020/04/01 9:57
 * @desc:
 */
public class SoftInputUtil {
    /**
     * @param main   根布局
     *
     */
    public static void addControl(final View main) {
        main.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                //1、获取main在窗体的可视区域
                main.getWindowVisibleDisplayFrame(rect);
                //2、输入键盘没有弹出时，获取main在窗体的不可视区域
                int mainInVisibleHeight = main.getHeight() - rect.bottom;
                if (mainInVisibleHeight > 100) {
                    int[] location = new int[2];
//                    scroll.getLocationInWindow(location);
//                    int scrollHeight = (location[1] + scroll.getHeight()) - rect.bottom;
//                    main.scrollTo(0, scrollHeight);
                } else {
                    main.scrollTo(0, 0);
                }
            }
        });
    }
}
