package com.hy.chatlibrary.listener;

import com.hy.chatlibrary.db.ChatMessage;

import java.util.List;

/**
 * @author:MtBaby
 * @date:2020/04/15 15:31
 * @desc:提供接口，对网络消息进行操作
 */
public interface OnNetMessageControl {
    void addMQMessage(ChatMessage chatMessage);

    void addNetMessages(List<ChatMessage> chatMessages);

    void onSendFail(ChatMessage message);

    void onSendSuccess(ChatMessage message);

}
