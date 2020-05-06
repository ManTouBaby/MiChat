package com.hy.chatlibrary.db;

import android.arch.persistence.room.TypeConverter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hy.chatlibrary.bean.MessageHolder;

import java.util.List;

/**
 * @author:MtBaby
 * @date:2020/04/29 18:38
 * @desc:
 */
public class InstructAcceptorConverter {

    @TypeConverter
    public List<MessageHolder> json2Object(String json) {
        return JSON.parseArray(json, MessageHolder.class);
    }

    @TypeConverter
    public String object2Json(List<MessageHolder> messageHolder) {
        return JSON.toJSONString(messageHolder, SerializerFeature.WriteMapNullValue);
    }
}
