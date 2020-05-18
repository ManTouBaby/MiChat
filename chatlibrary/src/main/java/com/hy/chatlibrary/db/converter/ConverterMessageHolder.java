package com.hy.chatlibrary.db.converter;

import android.arch.persistence.room.TypeConverter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hy.chatlibrary.bean.MessageHolder;

/**
 * @author:MtBaby
 * @date:2020/04/29 11:22
 * @desc:
 */
public class ConverterMessageHolder {
    @TypeConverter
    public MessageHolder json2Object(String json) {
        return JSON.parseObject(json, MessageHolder.class);
    }

    @TypeConverter
    public String object2Json(MessageHolder messageHolder) {
        return JSON.toJSONString(messageHolder, SerializerFeature.WriteMapNullValue);
    }
}
