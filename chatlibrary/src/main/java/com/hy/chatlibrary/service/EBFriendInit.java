package com.hy.chatlibrary.service;

import com.hy.chatlibrary.bean.MessageHolder;

import java.util.List;

/**
 * @author:MtBaby
 * @date:2020/05/26 14:55
 * @desc:
 */
public class EBFriendInit {
    private List<MessageHolder> messageHolders;
    private String errorLabel;

    public EBFriendInit(List<MessageHolder> messageHolders, String errorLabel) {
        this.messageHolders = messageHolders;
        this.errorLabel = errorLabel;
    }

    public List<MessageHolder> getMessageHolders() {
        return messageHolders;
    }

    public String getErrorLabel() {
        return errorLabel;
    }
}
