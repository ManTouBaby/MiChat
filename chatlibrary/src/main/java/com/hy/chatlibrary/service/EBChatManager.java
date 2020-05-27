package com.hy.chatlibrary.service;

import android.support.annotation.IntDef;

import com.hy.chatlibrary.db.entity.ChatMessage;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author:MtBaby
 * @date:2020/05/11 20:14
 * @desc://群成员、名称、显示名称等基本信息修改
 */
public class EBChatManager {
    private ChatMessage chatMessage;
    private String errorLabel;
    private int type;

    @IntDef({TYPE_UPDATE_CHAT_DISPLAY_ERROR,
            TYPE_UPDATE_CHAT_DISPLAY_SUCCESS,
            TYPE_ADD_MEMBER_FAIL,
            TYPE_ADD_MEMBER_SUCCESS,
            TYPE_EXIST_FAIL,
            TYPE_EXIST_SUCCESS,
            TYPE_UPDATE_GROUP_NAME_SUCCESS,
            TYPE_UPDATE_GROUP_NAME_FAIL,
            MQ_UPDATE_CHAT_DISPLAY_NAME, MQ_UPDATE_GROUP_NAME,
            MQ_ADD_MEMBER, MQ_EXIST_MEMBER})
    @Retention(RetentionPolicy.SOURCE)
    @interface UpdateChatDisplayNameStatus {
    }

    public final static int TYPE_UPDATE_CHAT_DISPLAY_ERROR = 1;
    public final static int TYPE_UPDATE_CHAT_DISPLAY_SUCCESS = 2;
    public final static int TYPE_UPDATE_GROUP_NAME_SUCCESS = 3;
    public final static int TYPE_UPDATE_GROUP_NAME_FAIL = 4;
    public final static int TYPE_ADD_MEMBER_FAIL = 5;
    public final static int TYPE_ADD_MEMBER_SUCCESS = 6;
    public final static int TYPE_EXIST_FAIL = 7;
    public final static int TYPE_EXIST_SUCCESS = 8;

    public final static int MQ_UPDATE_CHAT_DISPLAY_NAME = 9;
    public final static int MQ_UPDATE_GROUP_NAME = 10;
    public final static int MQ_ADD_MEMBER = 11;
    public final static int MQ_EXIST_MEMBER = 12;

    public EBChatManager(int type, ChatMessage chatMessage, String errorLabel) {
        this.chatMessage = chatMessage;
        this.errorLabel = errorLabel;
        this.type = type;
    }

    public String getErrorLabel() {
        return errorLabel;
    }

    public ChatMessage getChatMessage() {
        return chatMessage;
    }

    public int getType() {
        return type;
    }
}
