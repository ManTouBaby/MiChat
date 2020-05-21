package com.hy.chatlibrary.listener;

import com.hy.chatlibrary.bean.MessageHolder;
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

    void onUpdateChatDisplayNameFail(String mChatGroupId, MessageHolder messageHolder, String msg);

    void onUpdateChatDisplayNameSuccess(String mChatGroupId, MessageHolder messageHolder, String newChatGroupName);

    void onUpdateGroupNameFail(String mChatGroupId, MessageHolder messageHolder, String msg);

    void onUpdateGroupNameSuccess(String mChatGroupId, MessageHolder messageHolder, String newChatGroupName);

    void notifyRemove(ChatMessage message);
}
