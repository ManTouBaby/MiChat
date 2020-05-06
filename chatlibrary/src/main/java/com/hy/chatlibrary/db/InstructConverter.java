package com.hy.chatlibrary.db;

import android.arch.persistence.room.TypeConverter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * @author:MtBaby
 * @date:2020/04/29 11:31
 * @desc:
 */
public class InstructConverter {
    @TypeConverter
    public InstructBean json2Object(String json) {
        return JSON.parseObject(json, InstructBean.class);
    }

    @TypeConverter
    public String object2Json(InstructBean instructBO) {
        return JSON.toJSONString(instructBO, SerializerFeature.WriteMapNullValue);
    }
}
