package com.hy.chatlibrary.bean;

/**
 * @author:MtBaby
 * @date:2020/05/25 10:01
 * @desc:
 */
public class ChatGroupMembers {
    private int itemType;
    private MessageHolder messageHolder;

    public ChatGroupMembers(int itemType, MessageHolder messageHolder) {
        this.itemType = itemType;
        this.messageHolder = messageHolder;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public MessageHolder getMessageHolder() {
        return messageHolder;
    }

    public void setMessageHolder(MessageHolder messageHolder) {
        this.messageHolder = messageHolder;
    }
}
