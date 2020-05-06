package com.hy.chatlibrary.listener;

import com.hy.chatlibrary.db.ChatMessage;

/**
 * @author:MtBaby
 * @date:2020/04/20 10:44
 * @desc:
 */
public interface OnLocalMessageControl {
    void sendMessage(ChatMessage message);
}
