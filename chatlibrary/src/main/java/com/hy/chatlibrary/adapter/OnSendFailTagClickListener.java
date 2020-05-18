package com.hy.chatlibrary.adapter;

import com.hy.chatlibrary.db.entity.ChatMessage;

/**
 * @author:MtBaby
 * @date:2020/04/07 18:05
 * @desc:
 */
public interface OnSendFailTagClickListener {
    void onSendFailTagClick(ChatMessage message);
}
