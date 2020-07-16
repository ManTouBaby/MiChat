package com.hy.chatlibrary.listener;

import android.content.Context;

import com.hy.chatlibrary.bean.MessageHolder;

public interface OnChatPrivateListener {
    void sendPrivate(Context context, MessageHolder messageHolder);
}
