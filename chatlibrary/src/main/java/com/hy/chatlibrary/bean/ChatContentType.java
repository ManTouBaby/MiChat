package com.hy.chatlibrary.bean;

import android.support.annotation.DrawableRes;

import com.hy.chatlibrary.widget.chatinput.ContentType;

/**
 * @author:MtBaby
 * @date:2020/04/01 16:38
 * @desc:
 */
public class ChatContentType {
    private String typeName;
    private int typeIcon;
    private ContentType typeId;

    public ChatContentType(ContentType typeId, String typeName, @DrawableRes int typeIcon) {
        this.typeName = typeName;
        this.typeIcon = typeIcon;
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public int getTypeIcon() {
        return typeIcon;
    }

    public ContentType getTypeId() {
        return typeId;
    }
}
