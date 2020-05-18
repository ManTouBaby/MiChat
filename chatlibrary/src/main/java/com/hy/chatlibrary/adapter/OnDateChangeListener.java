package com.hy.chatlibrary.adapter;

import com.hy.chatlibrary.db.entity.ChatMessage;

import java.util.List;

/**
 * @author:MtBaby
 * @date:2020/04/05 17:41
 * @desc:
 */
public interface OnDateChangeListener {
    void addNewMessage(ChatMessage message);

    void updateMessage(ChatMessage message);

    void removeMessage(ChatMessage message);

    void addOldMessages(List<ChatMessage> chatMessages);

    void addNetMessages(List<ChatMessage> chatMessages);

    void onDestroy();
}
