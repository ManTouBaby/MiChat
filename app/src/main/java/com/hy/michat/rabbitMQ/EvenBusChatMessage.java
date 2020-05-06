package com.hy.michat.rabbitMQ;

import android.support.annotation.IntDef;

import com.hy.chatlibrary.db.ChatMessage;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author:MtBaby
 * @date:2020/04/22 18:11
 * @desc:
 */
public class EvenBusChatMessage {
    private ChatMessage chatMessage;
    private int mType;

    @IntDef({TYPE_ERROR, TYPE_SUCCESS, TYPE_MQ_MSG})
    @Retention(RetentionPolicy.SOURCE)
    @interface ChatMessageType {
    }

    public final static int TYPE_ERROR = 1;
    public final static int TYPE_SUCCESS = 2;
    public final static int TYPE_MQ_MSG = 3;

    public EvenBusChatMessage(ChatMessage chatMessage, @ChatMessageType int mType) {
        this.chatMessage = chatMessage;
        this.mType = mType;
    }

    public ChatMessage getChatMessage() {
        return chatMessage;
    }

    public int getType() {
        return mType;
    }
}
