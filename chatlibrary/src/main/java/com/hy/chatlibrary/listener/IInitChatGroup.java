package com.hy.chatlibrary.listener;

import com.hy.chatlibrary.db.entity.ChatMessage;

import java.util.List;

/**
 * @author:MtBaby
 * @date:2020/05/10 16:54
 * @desc:提供聊天列表初始化
 */
public interface IInitChatGroup {
    //初始化成功
    void initSuccess(List<ChatMessage> chatMessages);

    //初始化数据失败
    void initFaile(String label);
}
