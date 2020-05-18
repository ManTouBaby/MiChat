package com.hy.chatlibrary.adapter;

import com.hy.chatlibrary.db.entity.ChatMessage;

/**
 * @author:MtBaby
 * @date:2020/04/08 17:26
 * @desc:
 */
public interface OnPullDownLoadMoreListener {
    void onLoadMore(ChatMessage firstChatMessage);
}
