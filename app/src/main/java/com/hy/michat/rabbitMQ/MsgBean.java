package com.hy.michat.rabbitMQ;

import com.hy.chatlibrary.db.ChatMessage;

/**
 * @author:MtBaby
 * @date:2020/04/21 18:33
 * @desc:
 */
public class MsgBean {
    /**
     * data : {"duration":0,"fileSize":0,"holderDepartId":"111111","holderDepartName":"黑猫典狱","holderDuty":"典狱长","holderGender":0,"holderId":"test","holderMobile":"13829793334","holderName":"test","holderPortrait":"http://i0.hdslb.com/bfs/article/8e87829cde9559c8407892aa6110f83a4631c6b3.jpg","itemType":0,"latitude":0,"longitude":0,"messageCT":"2020-04-21 18:31:20","messageCTMillis":1587465080000,"messageContent":"旅途愉快","messageGroupId":"0001","messageGroupName":"测试聊天群","messageHolder":0,"messageId":"1121119534","messageLatitude":0,"messageLongitude":0,"messageST":"2020-04-21 18:31:20","messageSTMillis":1587465080000,"messageStatus":2,"player":false}
     * dataType : jstx
     * groupId : 0001
     * ifRead : 1
     * msgFrom : test
     * type : 0
     */

    private ChatMessage data;
    private String dataType;
    private String groupId;
    private String ifRead;
    private String msgFrom;
    private String type;

    public ChatMessage getData() {
        return data;
    }

    public void setData(ChatMessage data) {
        this.data = data;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getIfRead() {
        return ifRead;
    }

    public void setIfRead(String ifRead) {
        this.ifRead = ifRead;
    }

    public String getMsgFrom() {
        return msgFrom;
    }

    public void setMsgFrom(String msgFrom) {
        this.msgFrom = msgFrom;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
