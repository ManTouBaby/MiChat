package com.hy.chatlibrary.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.hy.chatlibrary.db.entity.ChatMessage;

import java.util.List;

/**
 * @author:MtBaby
 * @date:2020/04/08 11:45
 * @desc:
 */
@Dao
public interface ChatMessageDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertChatMessage(List<ChatMessage> messages);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertChatMessage(ChatMessage chatMessage);

    @Update
    void updateChatMessage(List<ChatMessage> messages);

    @Update
    void updateChatMessage(ChatMessage chatMessage);

    @Delete
    void deleteChatMessage(List<ChatMessage> message);


    @Delete
    void deleteChatMessage(ChatMessage message);

    @Query("Delete from chatmessage")
    void deleteAllMessage();

    //查询指定群聊的所有消息
    @Query("Select * from chatmessage where messageGroupId= :messageGroupId")
    List<ChatMessage> queryByGroupId(String messageGroupId);

    //查询指定群聊的最新消息
    @Query("Select * from chatmessage where messageGroupId= :messageGroupId order by messageSTMillis  asc limit 1")
    ChatMessage queryTopByGroupId(String messageGroupId);

    //查询指定群聊中显示名称包含指定字段或者内容包含包含指定字段的消息
    @Query("select * from ChatMessage where messageGroupId = :chatGroupId and  messageHolderShowName like  '%' || :label || '%'  or messageGroupId = :chatGroupId and   messageContent like '%' || :label || '%' order by messageSTMillis")
    List<ChatMessage> queryLikeLabel(String chatGroupId, String label);

    @Query("Select * from chatmessage where messageGroupId=:messageGroup and messageHolderId = :holderId")
    List<ChatMessage> queryByGroupIdAndHolderId(String messageGroup, String holderId);

    @Query("Select * from chatmessage where itemType=6 and messageGroupId=:messageGroup and messageHolderId = :holderId")
    List<ChatMessage> queryInstructByGroupIdAndHolderId(String messageGroup, String holderId);

    @Query("Select * from chatmessage where itemType=:itemType and messageGroupId=:messageGroup or itemType=:itemType2 and messageGroupId=:messageGroup")
    List<ChatMessage> queryHistoryByGroupId(int itemType, int itemType2, String messageGroup);

    @Query("Select * from chatmessage where itemType=:itemType  and messageGroupId=:messageGroup")
    List<ChatMessage> queryHistoryByGroupId(int itemType, String messageGroup);

    @Query("Select * from chatmessage where messageGroupId=:messageGroup and messageId = :messageId")
    ChatMessage queryByGroupIdAndMessageId(String messageGroup, String messageId);

    @Query("Select * from chatmessage where messageGroupId=:mChatGroupId order by messageSTMillis  desc limit :queryCount offset :startIndex")
    List<ChatMessage> queryMessageByCount(String mChatGroupId, int startIndex, int queryCount);

    //查询指定群聊指定时间之前的前queryCount个消息
    @Query("Select * from chatmessage  where messageGroupId=:mChatGroupId and messageSTMillis < :lastNewMilli order by messageSTMillis  desc limit :queryCount")
    List<ChatMessage> queryMessageByMilli(String mChatGroupId, long lastNewMilli, int queryCount);

    //查询指定群聊指定时间之前的前queryCount个消息
    @Query("Select * from chatmessage  where messageGroupId=:mChatGroupId and messageSTMillis >= :beforeMilli and messageSTMillis < :afterMilli order by messageSTMillis  desc ")
    List<ChatMessage> queryMessageByMilli(String mChatGroupId, long beforeMilli, long afterMilli);

    //查询图片、视屏、指令消息
    @Query("Select * from chatmessage where itemType=2 or itemType =3 or itemType = 6 order by messageSTMillis ")
    List<ChatMessage> queryMessageByPhotoAndVideo();

    //获取已同步的最新消息
    @Query("Select * from chatmessage where messageGroupId=:mChatGroupId and isSynchronization = 1 order by messageSTMillis  asc limit 1")
    ChatMessage queryMessageByTopAndSynchronization(String mChatGroupId);

    //获取最新消息
    @Query("Select * from chatmessage where messageGroupId=:mChatGroupId and  messageHolderId=:messageHolderId  order by messageSTMillis  asc limit 1")
    ChatMessage queryMMessageByTop(String mChatGroupId, String messageHolderId);

    //获取未同步的所有消息
    @Query("Select * from chatmessage where messageGroupId=:mChatGroupId and isSynchronization =0 order by messageSTMillis  asc limit 1")
    List<ChatMessage> queryAllMessageByUnchronization(String mChatGroupId);
}
