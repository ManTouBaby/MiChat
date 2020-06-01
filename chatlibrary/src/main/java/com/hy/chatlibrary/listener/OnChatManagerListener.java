package com.hy.chatlibrary.listener;

import com.hy.chatlibrary.db.entity.ChatMessage;

/**
 * @author:MtBaby
 * @date:2020/05/25 11:11
 * @desc:群管理监听
 */
public interface OnChatManagerListener {
    //修改群聊显示名称
    void changeChatDisplayName(String mChatGroupId,  ChatMessage chatMessage);

    //修改群聊显示名称
    void changeChatGroupName(String mChatGroupId, ChatMessage chatMessage);

    //修改群公告
    void changeChatGroupDesc(String mChatGroupId, ChatMessage chatMessage);

    //添加群成员
    void addChatGroupMember(String mChatGroupId, ChatMessage chatMessage);

    //退出群聊
    void existChatGroup(String mChatGroupId,ChatMessage chatMessage);
}
