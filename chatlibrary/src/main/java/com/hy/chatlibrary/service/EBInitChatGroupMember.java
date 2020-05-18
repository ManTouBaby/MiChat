package com.hy.chatlibrary.service;

import com.hy.chatlibrary.bean.MessageHolder;

import java.util.List;

/**
 * @author:MtBaby
 * @date:2020/05/11 10:15
 * @desc:
 */
public class EBInitChatGroupMember {
    private String errorLabel;
    private String initChatMessageID;
    private List<MessageHolder> messageHolders;

    public EBInitChatGroupMember(List<MessageHolder> messageHolders, String initChatMessageID, String errorLabel) {
        this.errorLabel = errorLabel;
        this.initChatMessageID = initChatMessageID;
        this.messageHolders = messageHolders;
    }

    public List<MessageHolder> getMessageHolders() {
        return messageHolders;
    }

    public String getErrorLabel() {
        return errorLabel;
    }

    public String getInitChatMessageID() {
        return initChatMessageID;
    }
}
