package com.hy.chatlibrary.service;

import android.support.annotation.IntDef;

import com.hy.chatlibrary.db.entity.ChatMessage;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author:MtBaby
 * @date:2020/04/22 18:11
 * @desc:
 */
public class EBChatMessageControl {
    private ChatMessage chatMessage;
    private String errorLabel;
    private int mType;

    @IntDef({TYPE_ERROR, TYPE_SUCCESS, TYPE_REMOVE_ERROR, TYPE_REMOVE_SUCCESS,TYPE_NOTIFY_REMOVE})
    @Retention(RetentionPolicy.SOURCE)
    @interface ChatMessageType {
    }

    public final static int TYPE_ERROR = 1;
    public final static int TYPE_SUCCESS = 2;
    public final static int TYPE_REMOVE_ERROR = 3;
    public final static int TYPE_REMOVE_SUCCESS = 4;
    public final static int TYPE_NOTIFY_REMOVE = 5;


    public EBChatMessageControl(@ChatMessageType int mType, ChatMessage chatMessage) {
        this(mType, chatMessage, null);
    }

    public EBChatMessageControl(@ChatMessageType int mType, ChatMessage chatMessage, String errorLabel) {
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
