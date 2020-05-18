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
public class EBChatGroupControl {
    @IntDef({TYPE_SEND_ERROR, TYPE_SEND_SUCCESS, TYPE_REMOVE_ERROR, TYPE_REMOVE_SUCCESS, TYPE_GROUP_INIT, TYPE_GROUP_MEMBER_INIT, TYPE_ADD_MQ, TYPE_MQ_UPDATE_CHAT_DISPLAY_NAME,TYPE_NOTIFY_REMOVE})
    @Retention(RetentionPolicy.SOURCE)
    @interface ChatGroupType {
    }

    public final static int TYPE_SEND_ERROR = 1;
    public final static int TYPE_SEND_SUCCESS = 2;
    public final static int TYPE_REMOVE_ERROR = 3;
    public final static int TYPE_REMOVE_SUCCESS = 4;
    public final static int TYPE_GROUP_INIT = 5;
    public final static int TYPE_GROUP_MEMBER_INIT = 6;
    public final static int TYPE_ADD_MQ = 7;
    public final static int TYPE_MQ_UPDATE_CHAT_DISPLAY_NAME = 8;//通知其他人更新群聊名称
    public final static int TYPE_NOTIFY_REMOVE = 9;//通知其他人更删除消息

    private String errorLabel;
    private ChatMessage chatMessage;
    private List<ChatMessage> chatMessages;
    private List<MessageHolder> messageHolders;
    private EBUpdateChatDisplayName updateChatDisplayName;
    private int type;

    //用于通知
    public EBChatGroupControl(int type, ChatMessage chatMessage) {
        this(type, chatMessage, null, null, null, null);
    }

    //用于通知
    public EBChatGroupControl(int type, ChatMessage chatMessage, String errorLabel) {
        this(type, chatMessage, null, null, null, errorLabel);
    }

    //用于更新群聊显示名称
    public EBChatGroupControl(int type,EBUpdateChatDisplayName updateChatDisplayName, String errorLabel) {
        this(type, null, null, null, updateChatDisplayName, errorLabel);
    }

    public EBChatGroupControl(int type, List<ChatMessage> chatMessages, List<MessageHolder> messageHolders, String errorLabel) {
        this(type, null, chatMessages, messageHolders, null, errorLabel);
    }

    private EBChatGroupControl(int type, ChatMessage chatMessage, List<ChatMessage> chatMessages, List<MessageHolder> messageHolders, EBUpdateChatDisplayName updateChatDisplayName, String errorLabel) {
        this.errorLabel = errorLabel;
        this.chatMessage = chatMessage;
        this.chatMessages = chatMessages;
        this.messageHolders = messageHolders;
        this.updateChatDisplayName = updateChatDisplayName;
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

    public EBUpdateChatDisplayName getUpdateChatDisplayName() {
        return updateChatDisplayName;
    }
}
