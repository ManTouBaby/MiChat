package com.hy.chatlibrary.service;

import android.support.annotation.IntDef;

import com.hy.chatlibrary.db.entity.ChatMessage;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author:MtBaby
 * @date:2020/04/22 18:11
 * @desc://消息发送、撤回等消息处理
 */
public class EBChatMessage {
    private ChatMessage chatMessage;
    private String errorLabel;
    private int mType;

    @IntDef({TYPE_SEND_ERROR, TYPE_SEND_SUCCESS, TYPE_REMOVE_ERROR, TYPE_REMOVE_SUCCESS,MQ_NOTIFY_REMOVE,MQ_NOTIFY_SEND})
    @Retention(RetentionPolicy.SOURCE)
    @interface ChatMessageType {
    }

    public final static int TYPE_SEND_ERROR = 1;
    public final static int TYPE_SEND_SUCCESS = 2;
    public final static int TYPE_REMOVE_ERROR = 3;
    public final static int TYPE_REMOVE_SUCCESS = 4;
    public final static int MQ_NOTIFY_REMOVE = 5;
    public final static int MQ_NOTIFY_SEND= 6;


    public EBChatMessage(@ChatMessageType int mType, ChatMessage chatMessage) {
        this(mType, chatMessage, null);
    }

    public EBChatMessage(@ChatMessageType int mType, ChatMessage chatMessage, String errorLabel) {
        this.mType = mType;
        this.errorLabel = errorLabel;
        this.chatMessage = chatMessage;
    }

    public ChatMessage getChatMessage() {
        return chatMessage;
    }

    public String getErrorLabel() {
        return errorLabel;
    }

    public int getType() {
        return mType;
    }
}
