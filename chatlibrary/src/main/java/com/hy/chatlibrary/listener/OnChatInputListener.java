package com.hy.chatlibrary.listener;

import com.hy.chatlibrary.db.ChatMessage;

/**
 * @author:MtBaby
 * @date:2020/04/04 23:24
 * @desc:提供到外部使用的接口，用于聊天框的初始化、消息发送、菜单栏按钮等监听
 */
public interface OnChatInputListener {
    //进行发送
    void onMessageSend(ChatMessage message, String chatMessageJson);

    //消息撤回监听
    void onChatMessageCallBack(ChatMessage message);

    //初始化列表
    void onInitChatList(ChatMessage chatMessage, OnNetMessageControl onNetMessageControl);

    void onDestroy();
}
