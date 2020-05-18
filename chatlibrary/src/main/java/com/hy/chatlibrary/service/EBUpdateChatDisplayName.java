package com.hy.chatlibrary.service;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author:MtBaby
 * @date:2020/05/11 20:14
 * @desc:
 */
public class EBUpdateChatDisplayName {
    private String mChatGroupId;
    private String memberId;
    private String newChatGroupName;
    private String errorLabel;
    private int status;

    @IntDef({TYPE_ERROR, TYPE_SUCCESS,UPDATE_MQ})
    @Retention(RetentionPolicy.SOURCE)
    @interface UpdateChatDisplayNameStatus {
    }

    public final static int TYPE_ERROR = 1;
    public final static int TYPE_SUCCESS = 2;
    public final static int UPDATE_MQ = 3;

    public EBUpdateChatDisplayName(@UpdateChatDisplayNameStatus int status,String mChatGroupId, String memberId, String newChatGroupName) {
        this.mChatGroupId = mChatGroupId;
        this.memberId = memberId;
        this.newChatGroupName = newChatGroupName;
        this.status = status;
    }

    public EBUpdateChatDisplayName(@UpdateChatDisplayNameStatus int status, String mChatGroupId, String memberId, String newChatGroupName,String errorLabel) {
        this.status = status;
        this.mChatGroupId = mChatGroupId;
        this.memberId = memberId;
        this.newChatGroupName = newChatGroupName;
        this.errorLabel = errorLabel;
    }

    public String getErrorLabel() {
        return errorLabel;
    }

    public int getStatus() {
        return status;
    }

    public String getmChatGroupId() {
        return mChatGroupId;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getNewChatGroupName() {
        return newChatGroupName;
    }
}
