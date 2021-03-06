package com.hy.chatlibrary.listener;

import com.hy.chatlibrary.db.entity.ChatMessage;

/**
 * @author:MtBaby
 * @date:2020/04/15 15:31
 * @desc:提供接口，对消息进行操作
 */
public interface IChatMessageControl {
    //发送消息失败
    void onSendFail(ChatMessage message, String msg);

    //发送消息成功
    void onSendSuccess(ChatMessage message);

    //消息撤回成功
    void onRemoveSuccess(ChatMessage message);

    //消息撤回失败
    void onRemoveFail(ChatMessage chatMessage, String msg);

    //更新群聊显示名称失败
    void onUpdateChatDisplayNameFail( ChatMessage chatMessage, String msg);

    //更新群聊显示名称成功
    void onUpdateChatDisplayNameSuccess(  ChatMessage chatMessage);

    //更新群名称失败
    void onUpdateGroupNameFail( ChatMessage chatMessage, String msg);

    //更新群名称成功
    void onUpdateGroupNameSuccess(  ChatMessage chatMessage);

    //更新群公告失败
    void onUpdateGroupDescFail( ChatMessage chatMessage, String msg);

    //更新群公告成功
    void onUpdateGroupDescSuccess(  ChatMessage chatMessage);

    //退群成功
    void onExistMemberSuccess( ChatMessage chatMessage);

    //退群失败
    void onExistMemberFail( ChatMessage chatMessage, String error);

    //加群成功
    void addMemberSuccess(  ChatMessage chatMessage);

    //加群失败
    void addMemberFail( ChatMessage chatMessage, String error);

    void notifyRemove(ChatMessage message);
}
