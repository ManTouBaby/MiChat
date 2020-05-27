package com.hy.chatlibrary.db;

import android.os.Handler;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.amap.api.location.AMapLocation;
import com.hy.chatlibrary.MiChatHelper;
import com.hy.chatlibrary.bean.MessageHolder;
import com.hy.chatlibrary.db.entity.ChatMessage;
import com.hy.chatlibrary.db.entity.InstructBean;
import com.hy.chatlibrary.listener.OnLocalMessageControl;
import com.hy.chatlibrary.utils.DateUtil;
import com.hy.chatlibrary.utils.UUIDUtil;

import java.io.File;
import java.util.List;

/**
 * @author:MtBaby
 * @date:2020/04/20 10:22
 * @desc:
 */
public class ChatMessageCreator {
    private MiChatHelper mMiChatHelper;
    private MessageHolder mMessageHolder;
    private Handler mHandler;
    private String mChatGroupId;
    private String mChatGroupName;
    private int mChatGroupType;
    private OnLocalMessageControl localMessageControl;

    public ChatMessageCreator(String mChatGroupId, String mChatGroupName, MiChatHelper mMiChatHelper, MessageHolder mMessageHolder, Handler mHandler, OnLocalMessageControl localMessageControl) {
        this.mMiChatHelper = mMiChatHelper;
        this.mMessageHolder = mMessageHolder;
        this.mHandler = mHandler;
        this.mChatGroupId = mChatGroupId;
        this.mChatGroupName = mChatGroupName;
        this.localMessageControl = localMessageControl;
    }


    //发送@
    public void createChatMessage(AMapLocation mAMapLocation, List<MessageHolder> messageHolders, String content, OnChatMessageCreateListener onChatMessageCreateListener) {
        createChatMessage(9, mAMapLocation, messageHolders, null, null, null, content, 0, null, null, 0, 0, onChatMessageCreateListener);
    }

    //发送指令
    public void createChatMessage(AMapLocation mAMapLocation, InstructBean instructBean, OnChatMessageCreateListener onChatMessageCreateListener) {
        createChatMessage(6, mAMapLocation, null, instructBean, null, null, "指令", 0, null, null, 0, 0, onChatMessageCreateListener);
    }

    //回复指令、引用
    public void createChatMessage(AMapLocation mAMapLocation, ChatMessage chatMessage, String textLabel, OnChatMessageCreateListener onChatMessageCreateListener) {
        int msgType = chatMessage.getItemType();
        createChatMessage(msgType == 6 ? 8 : 7, mAMapLocation, null, null, chatMessage, null, textLabel, 0, null, null, 0, 0, onChatMessageCreateListener);
    }

    //位置发送
    public void createChatMessage(AMapLocation mAMapLocation, String locationAddress, String locationRoad, double latitude, double longitude, OnChatMessageCreateListener messageCreateListener) {
        createChatMessage(4, mAMapLocation, null, null, null, null, "位置", 0, locationAddress, locationRoad, latitude, longitude, messageCreateListener);
    }

    //文件类型消息发送
    public void createChatMessage(int contentType, AMapLocation mAMapLocation, String content, String filePath, long duration, OnChatMessageCreateListener messageCreateListener) {
        createChatMessage(contentType, mAMapLocation, null, null, null, filePath, content, duration, null, null, 0, 0, messageCreateListener);
    }

    //文本发送 - 撤回消息提示、退出群聊、拉人入群
    public void createChatMessage(int contentType, AMapLocation mAMapLocation, String content, OnChatMessageCreateListener messageCreateListener) {
        createChatMessage(contentType, mAMapLocation, null, null, null, null, content, 0, null, null, 0, 0, messageCreateListener);
    }

    //修改群名称提示、修改群显示名称提示
    public void createChatMessage(int contentType, AMapLocation mAMapLocation, String content, String newLabel, OnChatMessageCreateListener messageCreateListener) {
        createChatMessage(contentType, mAMapLocation, null, null, null, null, content, 0, null, null, 0, 0, chatMessage -> {
            chatMessage.setLabel(newLabel);
            if (messageCreateListener != null) {
                messageCreateListener.chatMessageCreate(chatMessage);
            }
        });
    }

    private void createChatMessage(int contentType, AMapLocation mAMapLocation, List<MessageHolder> messageHolders, InstructBean instructBean, ChatMessage quoteMessage, String filePath, String content, long duration, String locationAddress, String locationRoad, double latitude, double longitude, OnChatMessageCreateListener messageCreateListener) {

        DateUtil.getNetTimeMilliByURL(mMiChatHelper.getNetTimeUrl(), mHandler, netMilli -> {
            ChatMessage chatMessage = new ChatMessage();
            if (contentType == 4) {
                chatMessage.setLocationAddress(locationAddress);
                chatMessage.setLocationRoad(locationRoad);
                chatMessage.setLatitude(latitude);
                chatMessage.setLongitude(longitude);
            }
            chatMessage.setMessageGroupId(mChatGroupId);
            chatMessage.setMessageGroupName(mChatGroupName);
            chatMessage.setMessageId(UUIDUtil.getUUID());
            chatMessage.setMessageOwner(0);
            chatMessage.setDuration(duration);
            chatMessage.setItemType(contentType);
            chatMessage.setInstructBean(instructBean);
            chatMessage.setChatMessage(quoteMessage);
            chatMessage.setMessageHolder(mMessageHolder);
            chatMessage.setMessageHolderId(mMessageHolder.getId());
            chatMessage.setMessageHolderName(mMessageHolder.getName());
            chatMessage.setMessageHolderShowName(mMessageHolder.getGroupName());
            chatMessage.setMessageHolders(messageHolders);
            File file = new File(content);
            if (file.exists()) {
                chatMessage.setFileName(file.getName());
                chatMessage.setFileSize(file.length());
            }
            chatMessage.setMessageContent(content);
            chatMessage.setMessageLocalPath(filePath);
            chatMessage.setMessageNetPath(filePath);
            if (mAMapLocation != null) {
                chatMessage.setMessageLatitude(mAMapLocation.getLatitude());
                chatMessage.setMessageLongitude(mAMapLocation.getLongitude());
                chatMessage.setMessageLocationAddress(mAMapLocation.getAddress());
                chatMessage.setMessageLocationRoad(mAMapLocation.getStreet() + mAMapLocation.getStreetNum());
            }
            chatMessage.setMessageCT(mMiChatHelper.isOpenNetTime() ? DateUtil.getStringTimeByMilli(netMilli) : DateUtil.getSystemTime());
            chatMessage.setMessageCTMillis(mMiChatHelper.isOpenNetTime() ? netMilli : DateUtil.getSystemTimeMilli());
            chatMessage.setMessageST(mMiChatHelper.isOpenNetTime() ? DateUtil.getStringTimeByMilli(netMilli) : DateUtil.getSystemTime());
            chatMessage.setMessageSTMillis(mMiChatHelper.isOpenNetTime() ? netMilli : DateUtil.getSystemTimeMilli());
            chatMessage.setMessageStatus(2);

            messageCreateListener.chatMessageCreate(chatMessage);
            if (localMessageControl != null) localMessageControl.sendMessage(chatMessage);
        });
    }

    public String getChatMessageJson(ChatMessage message) {
        String json = null;
//        PropertyFilter profilter = (object, name, value) -> {
//            if (name.equals("localFilePath")) {
//                // false表示字段将被排除在外
//                return false;
//            }
//            return true;
//        };
        SimplePropertyPreFilter profilter = new SimplePropertyPreFilter();
        profilter.getExcludes().add("localFilePath");
        json = JSONObject.toJSONString(message, profilter, SerializerFeature.WriteMapNullValue);
        return json;
    }

    public interface OnChatMessageCreateListener {
        void chatMessageCreate(ChatMessage chatMessage);
    }
}
