package com.hy.chatlibrary.service;

import android.support.annotation.IntDef;

import com.hy.chatlibrary.bean.MessageHolder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author:MtBaby
 * @date:2020/05/11 20:14
 * @desc:
 */
public class EBUpdateChat {
    private String mChatGroupId;
    private MessageHolder messageHolder;
    private String newChatGroupName;
    private String errorLabel;
    private int status;

    @IntDef({TYPE_ERROR, TYPE_SUCCESS, TYPE_UPDATE_GROUP_NAME_SUCCESS, TYPE_UPDATE_GROUP_NAME_FAIL, UPDATE_MQ, UPDATE_GROUP_NAME_MQ})
    @Retention(RetentionPolicy.SOURCE)
    @interface UpdateChatDisplayNameStatus {
    }

    public final static int TYPE_ERROR = 1;
    public final static int TYPE_SUCCESS = 2;
    public final static int TYPE_UPDATE_GROUP_NAME_SUCCESS = 3;
    public final static int TYPE_UPDATE_GROUP_NAME_FAIL = 4;
    public final static int UPDATE_MQ = 5;
    public final static int UPDATE_GROUP_NAME_MQ = 6;

    public EBUpdateChat(@UpdateChatDisplayNameStatus int status, String mChatGroupId, MessageHolder messageHolder, String newChatGroupName) {
        this.mChatGroupId = mChatGroupId;
        this.messageHolder = messageHolder;
        this.newChatGroupName = newChatGroupName;
        this.status = status;
    }

    public EBUpdateChat(@UpdateChatDisplayNameStatus int status, String mChatGroupId, MessageHolder messageHolderd, String newChatGroupName, String errorLabel) {
        this.status = status;
        this.mChatGroupId = mChatGroupId;
        this.messageHolder = messageHolderd;
        this.newChatGroupName = newChatGroupName;
        this.errorLabel = errorLabel;
    }

    public MessageHolder getMessageHolder() {
        return messageHolder;
    }

    public String getErrorLabel() {
        return errorLabel;
    }

    public int getStatus() {
        return status;
    }

    public String getChatGroupId() {
        return mChatGroupId;
    }

    public String getNewChatGroupName() {
        return newChatGroupName;
    }
}
