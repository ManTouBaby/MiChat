package com.hy.chatlibrary.adapter;

import android.view.View;

/**
 * @author:MtBaby
 * @date:2020/04/09 9:37
 * @desc:
 */
public interface OnItemChildClickListener<T> {
    void onItemChildClick(View view, T data);
}
