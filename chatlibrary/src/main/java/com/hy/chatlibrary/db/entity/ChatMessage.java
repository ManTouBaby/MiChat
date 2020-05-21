package com.hy.chatlibrary.db.entity;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.alibaba.fastjson.annotation.JSONField;
import com.hy.chatlibrary.base.BaseSmartBO;
import com.hy.chatlibrary.bean.MessageHolder;
import com.hy.chatlibrary.db.converter.ConverterChatMessage;
import com.hy.chatlibrary.db.converter.ConverterMessageHolder;
import com.hy.chatlibrary.db.converter.ConverterInstruct;
import com.hy.chatlibrary.db.converter.ConverterMessageHolders;

import java.util.List;

/**
 * @author:MtBaby
 * @date:2020/03/30 17:39
 * @desc:
 */
@Entity(indices = {@Index("messageId"), @Index("messageCTMillis")})
@TypeConverters({ConverterMessageHolder.class, ConverterInstruct.class, ConverterChatMessage.class, ConverterMessageHolders.class})
public class ChatMessage extends BaseSmartBO {

    //itemType: 自己的类型 0:文字 1:语音 2:视屏 3:图片 4:地图 5:文件 6：指令 7:引用 8:回复指令 9:@消息  10：撤回消息提示、修改群名称提示、修改群显示名称提示
    //itemType：别人的类型 100:文字 101:语音 102:视屏 103:图片 104:地图 105:文件 106：指令 107:引用 108:回复指令  9:@消息 110:撤回消息提示、修改群名称提示、修改群显示名称提示
    private String messageGroupId;//消息聊天组ID
    private String messageGroupName;//消息聊天组名称
    private String messageChatGroupDetail;//消息聊天描述
    private String messageGroupMaker;//消息聊天者创建人员

    @NonNull
    @PrimaryKey
    private String messageId;//消息ID
    private String messageCT;//消息生成时间
    private String messageST;//消息发送成功时间
    private String messageContent;//消息内容
    private String messageNetPath;//文件消息地址
    private long messageCTMillis;//消息生成时间戳
    private long messageSTMillis;//消息发送成功时间戳

    private String messageLocationAddress;//消息发送时的位置名称
    private String messageLocationRoad;//消息发送时的位置所在路段
    private double messageLatitude;//消息发送时的纬度
    private double messageLongitude;//消息发时送的经度


    //语音视频共有
    private long duration;//时长
    //语音、视频、图片、文件共有
    private float fileSize;//文件大小
    private String fileName;//文件名称

    //信息持有者
    private String messageHolderId;
    private String messageHolderName;
    private String messageHolderShowName;
    private MessageHolder messageHolder;
    private InstructBean instructBean;
    private ChatMessage chatMessage;//回复和引用对象
    private List<MessageHolder> messageHolders;//@消息接收人员

    //地图位置
    private String locationAddress;//位置名称
    private String locationRoad;//位置所在路段
    private double latitude;//纬度
    private double longitude;//经度

    private String reservedJson;//预留拓展JSON

    @Ignore
    @JSONField(serialize = false)
    private boolean isPlayer;//只用于语音，判断
    @JSONField(serialize = false)
    private String messageLocalPath;//本地缓存，用于存储本地的文件路径
    @JSONField(serialize = false)
    private int messageStatus;//消息发送状态  0:发送成功 1：发送失败 2:发送中
    @JSONField(serialize = false)
    private int messageOwner;//0:表示自己 1表示其他人
    @JSONField(serialize = false)
    private boolean isSynchronization;

//    @JSONField(serialize = false)
//    private String messageTo;//消息发送到
//    private String messageFrom;//消息来自


    public List<MessageHolder> getMessageHolders() {
        return messageHolders;
    }

    public void setMessageHolders(List<MessageHolder> messageHolders) {
        this.messageHolders = messageHolders;
    }

    public String getMessageHolderName() {
        return messageHolderName;
    }

    public void setMessageHolderName(String messageHolderName) {
        this.messageHolderName = messageHolderName;
    }

    public String getMessageHolderShowName() {
        return messageHolderShowName;
    }

    public void setMessageHolderShowName(String messageHolderShowName) {
        this.messageHolderShowName = messageHolderShowName;
    }

    public String getMessageHolderId() {
        return messageHolderId;
    }

    public void setMessageHolderId(String messageHolderId) {
        this.messageHolderId = messageHolderId;
    }

    public String getMessageNetPath() {
        return messageNetPath;
    }

    public void setMessageNetPath(String messageNetPath) {
        this.messageNetPath = messageNetPath;
    }

    public String getMessageLocalPath() {
        return messageLocalPath;
    }

    public void setMessageLocalPath(String messageLocalPath) {
        this.messageLocalPath = messageLocalPath;
    }

    public String getMessageChatGroupDetail() {
        return messageChatGroupDetail;
    }

    public void setMessageChatGroupDetail(String messageChatGroupDetail) {
        this.messageChatGroupDetail = messageChatGroupDetail;
    }

    public ChatMessage getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }

    public InstructBean getInstructBean() {
        return instructBean;
    }

    public void setInstructBean(InstructBean instructBean) {
        this.instructBean = instructBean;
    }

    public MessageHolder getMessageHolder() {
        return messageHolder;
    }

    public void setMessageHolder(MessageHolder messageHolder) {
        this.messageHolder = messageHolder;
    }

    public boolean isSynchronization() {
        return isSynchronization;
    }

    public void setSynchronization(boolean synchronization) {
        isSynchronization = synchronization;
    }

    public int getMessageOwner() {
        return messageOwner;
    }

    public void setMessageOwner(int messageOwner) {
        this.messageOwner = messageOwner;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public String getLocationRoad() {
        return locationRoad;
    }

    public void setLocationRoad(String locationRoad) {
        this.locationRoad = locationRoad;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getMessageLocationAddress() {
        return messageLocationAddress;
    }

    public void setMessageLocationAddress(String messageLocationAddress) {
        this.messageLocationAddress = messageLocationAddress;
    }

    public String getMessageGroupName() {
        return messageGroupName;
    }

    public void setMessageGroupName(String messageGroupName) {
        this.messageGroupName = messageGroupName;
    }

    public String getMessageLocationRoad() {
        return messageLocationRoad;
    }

    public void setMessageLocationRoad(String messageLocationRoad) {
        this.messageLocationRoad = messageLocationRoad;
    }

    public double getMessageLatitude() {
        return messageLatitude;
    }

    public void setMessageLatitude(double messageLatitude) {
        this.messageLatitude = messageLatitude;
    }

    public double getMessageLongitude() {
        return messageLongitude;
    }

    public void setMessageLongitude(double messageLongitude) {
        this.messageLongitude = messageLongitude;
    }

    public String getReservedJson() {
        return reservedJson;
    }

    public void setReservedJson(String reservedJson) {
        this.reservedJson = reservedJson;
    }

    public String getMessageGroupId() {
        return messageGroupId;
    }

    public void setMessageGroupId(String messageGroupId) {
        this.messageGroupId = messageGroupId;
    }

    public String getMessageGroupMaker() {
        return messageGroupMaker;
    }

    public void setMessageGroupMaker(String messageGroupMaker) {
        this.messageGroupMaker = messageGroupMaker;
    }

    public boolean isPlayer() {
        return isPlayer;
    }

    public void setPlayer(boolean player) {
        isPlayer = player;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public float getFileSize() {
        return fileSize;
    }

    public void setFileSize(float fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }


    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }


    public String getMessageCT() {
        return messageCT;
    }

    public void setMessageCT(String messageCT) {
        this.messageCT = messageCT;
    }

    public long getMessageCTMillis() {
        return messageCTMillis;
    }

    public void setMessageCTMillis(long messageCTMillis) {
        this.messageCTMillis = messageCTMillis;
    }

    public String getMessageST() {
        return messageST;
    }

    public void setMessageST(String messageST) {
        this.messageST = messageST;
    }

    public long getMessageSTMillis() {
        return messageSTMillis;
    }

    public void setMessageSTMillis(long messageSTMillis) {
        this.messageSTMillis = messageSTMillis;
    }

    public int getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(int messageStatus) {
        this.messageStatus = messageStatus;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "messageGroupId='" + messageGroupId + '\'' +
                ", messageGroupMaker='" + messageGroupMaker + '\'' +
                ", messageId='" + messageId + '\'' +
                ", messageHolder=" + messageOwner +
                ", messageContent='" + messageContent + '\'' +
                ", messageCT='" + messageCT + '\'' +
                ", messageCTMillis=" + messageCTMillis +
                ", messageST='" + messageST + '\'' +
                ", messageSTMillis=" + messageSTMillis +
                ", messageStatus=" + messageStatus +
                ", duration=" + duration +
                ", fileSize=" + fileSize +
                ", fileName='" + fileName + '\'' +
                ", locationAddress='" + locationAddress + '\'' +
                ", locationRoad='" + locationRoad + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", isPlayer=" + isPlayer +
                ", itemType=" + itemType +
                '}';
    }
}
