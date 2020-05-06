package com.hy.chatlibrary.base;

import android.content.res.Resources;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;


/**
 * @author:MtBaby
 * @date:2017/09/17 14:56
 * @desc:
 */


public class SmartVH extends RecyclerView.ViewHolder {

    public SmartVH(View itemView) {
        super(itemView);

        /**
         * 设置水波纹背景
         */
        if (itemView.getBackground() == null) {
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = itemView.getContext().getTheme();
            int top = itemView.getPaddingTop();
            int bottom = itemView.getPaddingBottom();
            int left = itemView.getPaddingLeft();
            int right = itemView.getPaddingRight();
            if (theme.resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true)) {
                itemView.setBackgroundResource(typedValue.resourceId);
            }
            itemView.setPadding(left, top, right, bottom);
        }
    }


    public void text(@IdRes int idRes, String label) {
        TextView textView;
        View view = itemView.findViewById(idRes);
        if (view instanceof TextView) {
            textView = (TextView) view;
            textView.setText(label);
        }
    }

    public void textColor(@IdRes int idRes, @ColorInt int color) {
        TextView textView;
        View view = itemView.findViewById(idRes);
        if (view instanceof TextView) {
            textView = (TextView) view;
            textView.setTextColor(color);
        }
    }

    public Button getButton(@IdRes int idRes) {
        return getViewById(idRes);
    }

    public TextView getText(@IdRes int idRes) {
        return getViewById(idRes);
    }

    public LinearLayout getLinearLayout(@IdRes int idRes) {
        return getViewById(idRes);
    }

    public RelativeLayout getRelativeLayout(@IdRes int idRes) {
        return getViewById(idRes);
    }

    public ImageView getImage(@IdRes int idRes) {
        return getViewById(idRes);
    }

    public View getItemView() {
        return itemView;
    }

    Map<Integer, View> viewMap = new HashMap<>();

    public <V extends View> V getViewById(int idRes) {
        View view = viewMap.get(idRes);
        if (view == null) {
            view = itemView.findViewById(idRes);
            viewMap.put(idRes, view);
        }
        return (V) view;
    }

}
