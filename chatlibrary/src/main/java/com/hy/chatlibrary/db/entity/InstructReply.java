package com.hy.chatlibrary.db.entity;

import com.hy.chatlibrary.bean.MessageHolder;

/**
 * @author:MtBaby
 * @date:2020/06/01 10:35
 * @desc:
 */
public class InstructReply {
    String id;//回复ID
    String messageGroupId;//指令所属群组
    String messageId;//指令消息ID
    MessageHolder instructReplier;//指令回复人员
}
