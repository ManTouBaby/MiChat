package com.hy.chatlibrary.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

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

    @Query("Delete from chatmessage")
    void deleteAllMessage();

    @Query("Select * from chatmessage")
    List<ChatMessage> queryAllChatMessage();

    @Query("Select * from chatmessage where messageGroupId= :messageGroupId")
    List<ChatMessage> queryAllChatMessage(String messageGroupId);

    @Query("Select * from chatmessage where messageId = :messageId")
    ChatMessage queryChatMessage(String messageId);

    @Query("Select * from chatmessage where messageGroupId=:mChatGroupId order by messageSTMillis  desc limit :queryCount offset :startIndex")
    List<ChatMessage> queryMessageByCount(String mChatGroupId, int startIndex, int queryCount);

    @Query("Select * from chatmessage where itemType=2 or itemType =3 order by messageSTMillis ")
    List<ChatMessage> queryMessageByPhotoAndVideo();

    //获取已同步的最新消息
    @Query("Select * from chatmessage where messageGroupId=:mChatGroupId and isSynchronization = 1 order by messageSTMillis  asc limit 1")
    ChatMessage queryMessageByTopAndSynchronization(String mChatGroupId);

    //获取未同步的所有消息
    @Query("Select * from chatmessage where messageGroupId=:mChatGroupId and isSynchronization =0 order by messageSTMillis  asc limit 1")
    List<ChatMessage> queryAllMessageByUnchronization(String mChatGroupId);
}
