package com.hy.chatlibrary.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

/**
 * @author:MtBaby
 * @date:2020/05/10 21:55
 * @desc:消息免打扰实体
 */
@Entity(indices = {@Index("chatGroupId"), @Index("chatGroupHolderID")}, primaryKeys = {"chatGroupHolderID", "chatGroupId"})
public class NoDisturbing {
    @NonNull
    private String chatGroupHolderID;//设置免打扰人员ID
    @NonNull
    private String chatGroupId;//群组
    private boolean isOpen;//是否开启免打扰

    @NonNull
    public String getChatGroupHolderID() {
        return chatGroupHolderID;
    }

    public void setChatGroupHolderID(@NonNull String chatGroupHolderID) {
        this.chatGroupHolderID = chatGroupHolderID;
    }

    @NonNull
    public String getChatGroupId() {
        return chatGroupId;
    }

    public void setChatGroupId(@NonNull String chatGroupId) {
        this.chatGroupId = chatGroupId;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }
}
