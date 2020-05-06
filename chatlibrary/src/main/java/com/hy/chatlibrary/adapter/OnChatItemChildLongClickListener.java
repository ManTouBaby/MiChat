package com.hy.chatlibrary.adapter;

import android.view.View;

import com.hy.chatlibrary.db.ChatMessage;

/**
 * @author:MtBaby
 * @date:2020/04/09 9:37
 * @desc:
 */
public interface OnChatItemChildLongClickListener {
    void onItemChildLongClick(View view, ChatMessage chatMessage);
}
