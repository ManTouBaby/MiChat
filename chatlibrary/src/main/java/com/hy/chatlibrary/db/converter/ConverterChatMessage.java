package com.hy.chatlibrary.db.converter;

import android.arch.persistence.room.TypeConverter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hy.chatlibrary.db.entity.ChatMessage;

/**
 * @author:MtBaby
 * @date:2020/05/08 9:19
 * @desc:
 */
public class ConverterChatMessage {
    @TypeConverter
    public ChatMessage json2Object(String json) {
        return JSON.parseObject(json, ChatMessage.class);
    }

    @TypeConverter
    public String object2Json(ChatMessage chatMessage) {
        return JSON.toJSONString(chatMessage, SerializerFeature.WriteMapNullValue);
    }
}
