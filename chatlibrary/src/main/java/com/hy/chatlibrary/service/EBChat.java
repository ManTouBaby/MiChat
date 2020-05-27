package com.hy.chatlibrary.service;

import android.support.annotation.IntDef;

import com.hy.chatlibrary.bean.MessageHolder;
import com.hy.chatlibrary.db.entity.ChatMessage;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * @author:MtBaby
 * @date:2020/05/10 20:04
 * @desc:
 */
public class EBChat {
    @IntDef({TYPE_SEND_ERROR, TYPE_SEND_SUCCESS,
            TYPE_REMOVE_ERROR, TYPE_REMOVE_SUCCESS,
            TYPE_GROUP_INIT,
            TYPE_GROUP_MEMBER_INIT,
            MQ_NEW_MSG,
            MQ_UPDATE_CHAT_DISPLAY_NAME,
            MQ_MSG_CALL_BACK,
            MQ_ADD_MEMBER,
            MQ_EXIST_CHAT})
    @Retention(RetentionPolicy.SOURCE)
    @interface ChatGroupType {
    }

    public final static int TYPE_SEND_ERROR = 1;
    public final static int TYPE_SEND_SUCCESS = 2;
    public final static int TYPE_REMOVE_ERROR = 3;
    public final static int TYPE_REMOVE_SUCCESS = 4;
    public final static int TYPE_GROUP_INIT = 5;
    public final static int TYPE_GROUP_MEMBER_INIT = 6;
    public final static int TYPE_FRIEND_LIST_INIT = 7;
    public final static int MQ_NEW_MSG = 8;
    public final static int MQ_UPDATE_CHAT_DISPLAY_NAME = 9;//通知其他人更新群聊名称
    public final static int MQ_MSG_CALL_BACK = 10;//通知其他人删除消息
    public final static int MQ_ADD_MEMBER = 11;//通知新增成员
    public final static int MQ_EXIST_CHAT = 12;//通知退群

    private String errorLabel;
    private ChatMessage chatMessage;
    private List<ChatMessage> chatMessages;
    private List<MessageHolder> messageHolders;
    private EBChatManager ebChatManager;
    private EBFriendInit ebFriendInit;
    private int type;

    //用于通知
    public EBChat(int type, ChatMessage chatMessage) {
        this(type, chatMessage, null, null, null,null, null);
    }

    //用于通知
    public EBChat(int type, ChatMessage chatMessage, String errorLabel) {
        this(type, chatMessage, null, null,null, null, errorLabel);
    }

    //用于更新群聊显示名称
    public EBChat(int type, EBChatManager updateChatDisplayName, String errorLabel) {
        this(type, null, null, null,null, updateChatDisplayName, errorLabel);
    }

    public EBChat(int type, EBFriendInit ebFriendInit, String errorLabel) {
        this(type, null, null, null,ebFriendInit, null, errorLabel);
    }

    public EBChat(int type, List<ChatMessage> chatMessages, List<MessageHolder> messageHolders, String errorLabel) {
        this(type, null, chatMessages, messageHolders,null, null, errorLabel);
    }

    private EBChat(int type, ChatMessage chatMessage, List<ChatMessage> chatMessages, List<MessageHolder> messageHolders,EBFriendInit ebFriendInit, EBChatManager ebChatManager, String errorLabel) {
        this.errorLabel = errorLabel;
        this.chatMessage = chatMessage;
        this.chatMessages = chatMessages;
        this.messageHolders = messageHolders;
        this.ebChatManager = ebChatManager;
        this.ebFriendInit = ebFriendInit;
        this.type = type;
    }

    public String getErrorLabel() {
        return errorLabel;
    }

    public ChatMessage getChatMessage() {
        return chatMessage;
    }

    public List<ChatMessage> getChatMessages() {
        return chatMessages;
    }

    public int getType() {
        return type;
    }

    public List<MessageHolder> getMessageHolders() {
        return messageHolders;
    }

    public EBChatManager getEbChatManager() {
        return ebChatManager;
    }

    public EBFriendInit getEbFriendInit() {
        return ebFriendInit;
    }
}
