package com.hy.chatlibrary.listener;

import com.hy.chatlibrary.db.entity.ChatMessage;

/**
 * @author:MtBaby
 * @date:2020/05/10 11:54
 * @desc:用于获取MQ通知
 */
public interface IMQMessageControl {
    void addMQMessage(ChatMessage chatMessage);
}
