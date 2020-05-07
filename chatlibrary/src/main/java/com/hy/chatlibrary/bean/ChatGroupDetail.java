package com.hy.chatlibrary.bean;

import java.util.ArrayList;

/**
 * @author:MtBaby
 * @date:2020/05/07 10:04
 * @desc:
 */
public class ChatGroupDetail {
    private String messageGroupId;//消息聊天组ID
    private String messageGroupName;//消息聊天组名称
    private String messageGroupDes;//消息聊天组描述
    private String messageGroupMaker;//消息聊天者创建人员

    private ArrayList<MessageHolder> groupMembers;//聊天组成员

    public String getMessageGroupId() {
        return messageGroupId;
    }

    public void setMessageGroupId(String messageGroupId) {
        this.messageGroupId = messageGroupId;
    }

    public String getMessageGroupName() {
        return messageGroupName;
    }

    public void setMessageGroupName(String messageGroupName) {
        this.messageGroupName = messageGroupName;
    }

    public String getMessageGroupDes() {
        return messageGroupDes;
    }

    public void setMessageGroupDes(String messageGroupDes) {
        this.messageGroupDes = messageGroupDes;
    }

    public String getMessageGroupMaker() {
        return messageGroupMaker;
    }

    public void setMessageGroupMaker(String messageGroupMaker) {
        this.messageGroupMaker = messageGroupMaker;
    }

    public ArrayList<MessageHolder> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(ArrayList<MessageHolder> groupMembers) {
        this.groupMembers = groupMembers;
    }
}
