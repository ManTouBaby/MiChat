package com.hy.chatlibrary.utils;

import android.app.Activity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hrw.chatlibrary.R;

/**
 * @author:MtBaby
 * @date:2020/05/26 21:30
 * @desc:
 */
public class LoadingHelper {
    private static PopupWindow popupWindow;

    public static void showLoading(Activity context) {
        showLoading(context, null);
    }

    //弹出加载框
    public static void showLoading(Activity context, String label) {
        View view;
        if (popupWindow != null && popupWindow.isShowing()) {
            view = popupWindow.getContentView();
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.popup_dialog_loading, null);
        }
        if (!TextUtils.isEmpty(label)) {
            TextView loadLabel = view.findViewById(R.id.mi_load_label);
            loadLabel.setText(label);
        }
        if (popupWindow != null && popupWindow.isShowing()) return;
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(context.findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
        StatusBarUtil.setBackgroundAlpha(context, 0.5f);
        popupWindow.setOnDismissListener(() -> {
            StatusBarUtil.setBackgroundAlpha(context, 1f);
        });
    }

    public static void closeLoading() {
        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }
}
