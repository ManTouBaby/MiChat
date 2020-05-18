package com.hy.chatlibrary.listener;

import com.hy.chatlibrary.db.entity.ChatMessage;

/**
 * @author:MtBaby
 * @date:2020/04/15 15:31
 * @desc:提供接口，对消息进行操作
 */
public interface IChatMessageControl {

    void onSendFail(ChatMessage message, String msg);

    void onSendSuccess(ChatMessage message);

    void onRemoveSuccess(ChatMessage message);

    void onRemoveFail(ChatMessage chatMessage, String msg);

    void onUpdateChatDisplayNameFail(String mChatGroupId, String memberId, String msg);

    void onUpdateChatDisplayNameSuccess(String mChatGroupId, String memberId, String newChatGroupName);

    void notifyRemove(ChatMessage message);
}
