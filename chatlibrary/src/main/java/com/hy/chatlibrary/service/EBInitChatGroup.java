package com.hy.chatlibrary.service;

import com.hy.chatlibrary.db.entity.ChatMessage;

import java.util.List;

/**
 * @author:MtBaby
 * @date:2020/05/10 17:20
 * @desc:
 */
public class EBInitChatGroup {
    private String errorLabel;
    private String  initChatMessageID;
    private List<ChatMessage> chatMessages;


    public EBInitChatGroup(List<ChatMessage> chatMessages, String initChatMessageID,String errorLabel) {
        this.errorLabel = errorLabel;
        this.chatMessages = chatMessages;
        this.initChatMessageID = initChatMessageID;
    }



    public String getErrorLabel() {
        return errorLabel;
    }

    public List<ChatMessage> getChatMessages() {
        return chatMessages;
    }

    public String getInitChatMessageID() {
        return initChatMessageID;
    }
}
